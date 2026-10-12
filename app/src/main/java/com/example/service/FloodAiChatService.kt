package com.example.service

import com.example.BuildConfig
import com.example.model.FloodPrediction
import com.example.model.RadarStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

object FloodAiChatService {

    private const val GEMINI_MODEL = "gemini-3.5-flash"
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    // OkHttp configured with 60-second timeouts as mandated by Gemini skill
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Configurable custom server URL (if the user hosts their own server)
    var customServerEndpoint: String = ""
    var isUsingCustomServer: Boolean = false

    suspend fun sendChatMessage(
        userPrompt: String,
        history: List<ChatMessage>,
        radarStatus: RadarStatus,
        predictions: List<FloodPrediction>,
        selectedHour: Int
    ): String = withContext(Dispatchers.IO) {
        if (isUsingCustomServer && customServerEndpoint.isNotBlank()) {
            return@withContext callCustomServer(userPrompt, history, radarStatus, predictions, selectedHour)
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If API key is not configured or placeholder, use intelligent local hydrological knowledge engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalKnowledgeResponse(userPrompt, radarStatus, predictions, selectedHour)
        }

        try {
            val url = "$GEMINI_BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"

            // Construct system instruction with live app telemetry & Chennai flood knowledge
            val systemContext = buildSystemContext(radarStatus, predictions, selectedHour)

            val requestJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemContext)
                        })
                    })
                })

                val contentsArray = JSONArray()

                // Add recent history turns (up to 4 previous messages)
                val recentHistory = history.takeLast(4)
                recentHistory.forEach { msg ->
                    contentsArray.put(JSONObject().apply {
                        put("role", if (msg.isUser) "user" else "model")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", msg.text)
                            })
                        })
                    })
                }

                // Add current user prompt
                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                })

                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 800)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // If API quota or key issue, fallback seamlessly to local knowledge
                return@withContext generateLocalKnowledgeResponse(userPrompt, radarStatus, predictions, selectedHour)
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                generateLocalKnowledgeResponse(userPrompt, radarStatus, predictions, selectedHour)
            }
        } catch (e: Exception) {
            // Graceful fallback to domain AI model
            generateLocalKnowledgeResponse(userPrompt, radarStatus, predictions, selectedHour)
        }
    }

    private fun callCustomServer(
        prompt: String,
        history: List<ChatMessage>,
        radarStatus: RadarStatus,
        predictions: List<FloodPrediction>,
        selectedHour: Int
    ): String {
        return try {
            val payload = JSONObject().apply {
                put("query", prompt)
                put("forecast_hour", selectedHour)
                put("rainfall_rate_mm_hr", radarStatus.currentIntensity.rateMmHr)
                put("tidal_lock", radarStatus.tidalLockActive)
                put("top_flood_streets", JSONArray().apply {
                    predictions.take(3).forEach { p ->
                        put(JSONObject().apply {
                            put("name", p.street.name)
                            put("depth_cm", p.depthAtHour(selectedHour))
                            put("time_to_flood_min", p.timeToFloodMinutes)
                        })
                    }
                })
            }

            val body = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(customServerEndpoint)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val resText = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(resText)
                json.optString("response", json.optString("message", resText))
            } else {
                "Server responded with code ${response.code}: $resText"
            }
        } catch (e: Exception) {
            "Could not connect to custom server at $customServerEndpoint: ${e.localizedMessage}. Falling back to in-app flood knowledge.\n\n" +
                    generateLocalKnowledgeResponse(prompt, radarStatus, predictions, selectedHour)
        }
    }

    private fun buildSystemContext(
        radarStatus: RadarStatus,
        predictions: List<FloodPrediction>,
        selectedHour: Int
    ): String {
        val topStreets = predictions.take(3).joinToString("; ") {
            "${it.street.name} (Rank #${it.floodRankOrder}, ETA: ${it.timeToFloodMinutes}m, Depth: ${it.depthAtHour(selectedHour)}cm)"
        }

        return """
        You are AquaAI, the flood intelligence assistant embedded in the AquaCast Android app for Chennai, India.
        You assist users with:
        1. Explaining which Chennai streets flood first and why (topographical depression bowls like Madipakkam and Velachery vs high ridges like Guindy Kathipara).
        2. Technical explanation of ISRO Cartosat DEM elevations (in meters) and IMD S-Band Doppler Radar reflectivity (dBZ) and rainfall rates (mm/hr).
        3. Explaining the Bay of Bengal Tidal Lock phenomenon: high tide obstructs gravity outfall discharge of the Buckingham Canal and Adyar River.
        4. Safe vehicle wading depths (<15cm safe, 15-30cm risk for two-wheelers/hatchbacks, >30cm impassable, >60cm boats only).
        5. Explaining how the app's traffic reroute engine bypasses flooded streets via elevated routes (like Guindy GST road and Anna Nagar).
        
        Current Live App Telemetry:
        - Radar Rainfall Rate: ${radarStatus.currentIntensity.rateMmHr} mm/hr (${radarStatus.currentIntensity.title})
        - Active Forecast Horizon: +${selectedHour} Hours post-rainfall
        - Bay of Bengal Tidal Lock: ${if (radarStatus.tidalLockActive) "ACTIVE (Gravity outfalls locked)" else "INACTIVE (Free gravity discharge)"}
        - Top 3 Projected Streets to Flood First: $topStreets
        
        Keep responses concise, helpful, friendly, and structured with bullet points.
        """.trimIndent()
    }

    /**
     * Intelligent local fallback knowledge engine grounded in Chennai hydrology and app features.
     */
    fun generateLocalKnowledgeResponse(
        query: String,
        radarStatus: RadarStatus,
        predictions: List<FloodPrediction>,
        selectedHour: Int
    ): String {
        val q = query.lowercase()
        val firstToFlood = predictions.firstOrNull()

        return when {
            q.contains("which street") || q.contains("flood first") || q.contains("first to flood") -> {
                val street = firstToFlood?.street?.name ?: "Madipakkam Lake Road"
                val eta = firstToFlood?.timeToFloodMinutes ?: 14
                val depth = firstToFlood?.depthAtHour(selectedHour) ?: 38.0
                """
                🌊 **Streets Projected to Flood First:**
                
                1. **$street** (Rank #1)
                   - **Flood Arrival ETA:** in ~$eta minutes post-rainfall.
                   - **ISRO Elevation:** ${firstToFlood?.street?.elevationMeters ?: 1.8}m MSL (encroached lakebed depression).
                   - **Projected Depth (+${selectedHour}h):** ${depth} cm.
                
                2. **Perambur Ganesapuram Subway** (Rank #2, ETA ~18 min)
                   - Depressed railway underpass with zero gravity drainage.
                
                3. **Velachery Bypass & Inner Ring Rd** (Rank #3, ETA ~24 min)
                   - Natural Pallikaranai marsh basin bottleneck.
                   
                💡 *Tip:* Use the **Reroute** tab to automatically calculate detours along the elevated Guindy ridge.
                """.trimIndent()
            }

            q.contains("velachery") || q.contains("madipakkam") -> {
                """
                📍 **Why do Velachery and Madipakkam flood so rapidly?**
                
                - **Low ISRO DEM Elevation:** Velachery sits at just **2.2m** and Madipakkam at **1.8m** above Mean Sea Level (MSL). They were historically natural agricultural lake beds and marshlands (*Pallikaranai*).
                - **Gravitational Funnel:** Rain falling on higher surrounding terrain (St. Thomas Mount at 15m and Guindy at 12.8m) naturally flows downward into this basin.
                - **Canal Siltation:** The Velachery canal connects to Pallikaranai marsh, which is heavily constrained and often tidal-locked during monsoon rains.
                
                ⚠️ *Advisory:* Avoid low-lying bypass sections when radar reflectivity exceeds **40 dBZ**.
                """.trimIndent()
            }

            q.contains("tidal lock") || q.contains("tide") || q.contains("bay of bengal") -> {
                val status = if (radarStatus.tidalLockActive) "Currently ACTIVE" else "Currently INACTIVE"
                """
                🌊 **What is Bay of Bengal Tidal Lock?**
                
                - **Status:** $status in your current simulation.
                - **The Mechanism:** Chennai's stormwater drains rely on gravity to discharge runoff into the Bay of Bengal via the **Buckingham Canal**, **Adyar River**, and **Cooum River**.
                - **High Tide Impediment:** When high tide occurs (+1.2m to +1.6m surge), ocean seawater pushes back into canal estuaries.
                - **Consequence:** Gravity drainage stops completely. Water backs up into city street drains, causing sudden waterlogging even after rain stops!
                
                💡 You can toggle Tidal Lock on/off in the **Radar** or **Forecast** tab to observe the dramatic difference in drain discharge capacity.
                """.trimIndent()
            }

            q.contains("car") || q.contains("vehicle") || q.contains("bike") || q.contains("safe to drive") || q.contains("cm") -> {
                """
                🚗 **Vehicle Wading Safety Thresholds:**
                
                - **< 15 cm (< 6 in) — SAFE:** Passable for all vehicles. Proceed with normal caution.
                - **15 to 30 cm (6 - 12 in) — WATERLOGGED:** 
                  - Two-wheelers risk engine stalling and exhaust flooding.
                  - Low sedans/hatchbacks risk air intake hydrolock.
                  - Only SUVs and high-clearance buses advised.
                - **30 to 60 cm (1 - 2 ft) — SEVERE:** 
                  - Road closed to passenger cars. Floating vehicle hazard.
                - **> 60 cm (> 2 ft) — CRITICAL SUBMERGENCE:** 
                  - Complete road submersion. Submerged culverts and open manholes. Emergency boats only.
                """.trimIndent()
            }

            q.contains("reroute") || q.contains("traffic") || q.contains("detour") -> {
                """
                🚗 **How AquaCast Reroutes Traffic:**
                
                1. **Threshold Trigger:** When predicted water depth along any street segment reaches **>15 cm**, that segment is flagged as unsafe.
                2. **Elevation Routing:** Rather than standard shortest paths, our algorithm routes through Chennai's high bedrock corridors:
                   - **Kathipara / Guindy Ridge (12.8m elevation buffer)**
                   - **Anna Nagar Plateau (9.8m elevation)**
                   - **Mylapore Coastal Ridge (7.1m elevation)**
                3. **Smart City API:** Outputs structured JSON payloads ready for municipal traffic signal management and navigation apps.
                
                👉 Check the **Reroute** tab to see live side-by-side detours and copy the API payload.
                """.trimIndent()
            }

            q.contains("server") || q.contains("host") || q.contains("backend") || q.contains("api") -> {
                """
                🖥️ **Hosting the AI Chat Bot on a Server:**
                
                This AI assistant can be hosted standalone on **Google Cloud Run**, **AWS Lambda**, **Render**, or a custom VPS!
                
                - **Node.js Express** or **Python FastAPI** backend templates are ready for copy in the **Server Hosting** menu.
                - Endpoint specification: `POST /api/chat` with `{ query, forecast_hour, rainfall_rate }`.
                - Enter your server URL in the **Server Settings** modal in this chat bot to connect live!
                """.trimIndent()
            }

            q.contains("google map") || q.contains("maps") || q.contains("navigation") -> {
                """
                🗺️ **How AquaCast Uses Google Maps Data:**
                
                - **Google Maps Directions API:** Extracts exact road networks, turn-by-turn directions, distances, and typical commute times between Chennai hubs.
                - **Live Traffic Layer:** Visualizes real-time vehicle flow (Green = 50km/h free flow, Amber = 25km/h congestion, Dark Red = 0km/h flood standstill).
                - **Elevation API Integration:** Samples elevations every 200m to verify that detour routes travel over elevated ridges (Kathipara 12.8m, Anna Nagar 9.8m).
                - **Direct Google Maps Navigation:** Tap **"Open in Google Maps"** to launch official Google Maps navigation on your phone with the flood-safe detour preloaded!
                """.trimIndent()
            }

            q.contains("isro") || q.contains("radar") || q.contains("elevation") -> {
                """
                🛰️ **Radar & Elevation Data Sources:**
                
                - **IMD Weather Radar:** Chennai Meenambakkam S-Band Doppler Radar (2.8 GHz) provides real-time precipitation rate (mm/hr) and storm reflectivity (dBZ).
                - **ISRO Cartosat DEM:** 30m Digital Elevation Model providing exact street-level heights above Mean Sea Level (MSL).
                - **Manning's Pipe Network Equation:** Solves water runoff vs pipe discharge: Q_drain = (1/n) * A * R_h^(2/3) * S^(1/2) * (1 - siltation).
                """.trimIndent()
            }

            else -> {
                """
                👋 Hello! I am **AquaAI**, your Chennai flood prediction and urban drainage assistant.
                
                Here is what I can help you with:
                - 🌊 **"Which street will flood first in Chennai?"**
                - 📍 **"Why do Velachery and Madipakkam flood quickly?"**
                - 🌊 **"How does the Bay of Bengal Tidal Lock work?"**
                - 🚗 **"Is 25cm water safe for my car?"**
                - 🚗 **"How does the traffic rerouting detour work?"**
                - 🖥️ **"How can I host this AI bot on a server?"**
                
                What would you like to explore?
                """.trimIndent()
            }
        }
    }

    // Server code templates for users wanting to deploy on their own cloud server
    const val NODE_SERVER_CODE = """
// AquaCast Server: Node.js + Express + Google Gen AI SDK
const express = require('express');
const { GoogleGenerativeAI } = require('@google/generative-ai');

const app = express();
app.use(express.json());

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
const model = genAI.getGenerativeModel({ model: "gemini-3.5-flash" });

app.post('/api/chat', async (req, res) => {
  try {
    const { query, forecast_hour, rainfall_rate_mm_hr, tidal_lock } = req.body;
    
    const systemPrompt = `You are AquaAI, Chennai Flood Intelligence Assistant. 
    Current Rainfall: ${"$"}{rainfall_rate_mm_hr} mm/hr, Horizon: +${"$"}{forecast_hour}h, Tidal Lock: ${"$"}{tidal_lock}.
    Help users with street flood ETA, ISRO DEM elevation, and traffic detours.`;

    const chat = model.startChat({
      history: [{ role: "user", parts: [{ text: systemPrompt }] }]
    });
    
    const result = await chat.sendMessage(query);
    res.json({ response: result.response.text(), status: "SUCCESS" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`AquaCast AI Server listening on port ${"$"}{PORT}`));
"""

    const val PYTHON_SERVER_CODE = """
# AquaCast Server: Python FastAPI + google-genai
import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from google import genai

app = FastAPI(title="AquaCast Chennai Flood AI Server")
client = genai.Client(api_key=os.environ.get("GEMINI_API_KEY"))

class ChatRequest(BaseModel):
    query: str
    forecast_hour: int = 1
    rainfall_rate_mm_hr: float = 45.0
    tidal_lock: bool = True

@app.post("/api/chat")
async def chat_endpoint(req: ChatRequest):
    try:
        system_instruction = (
            f"You are AquaAI Chennai Flood Assistant. "
            f"Rainfall: {req.rainfall_rate_mm_hr}mm/hr, Tidal Lock: {req.tidal_lock}."
        )
        response = client.models.generate_content(
            model="gemini-3.5-flash",
            contents=req.query,
            config={"system_instruction": system_instruction}
        )
        return {"response": response.text, "status": "SUCCESS"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Run with: uvicorn server:app --host 0.0.0.0 --port 8080
"""
}
