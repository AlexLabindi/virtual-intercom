import React, { useState, useEffect } from 'react';

/**
 * OwnerDashboard Component - Main monitoring hub open on the owner's home PC browser.
 * Listens to real-time ring notifications via WebSocket.
 */
export default function OwnerDashboard() {
    const [incomingCall, setIncomingCall] = useState(false);
    const [callStatus, setCallStatus] = useState('LISTENING'); // LISTENING, TALKING

  useEffect(() => {
  console.log("Tentativo di connessione al WebSocket...");
  const socket = new WebSocket('ws://localhost:8090/ws/signaling');

  socket.onopen = () => {
    console.log("✅ WebSocket connesso con successo al backend!");
  };

      socket.onmessage = (event) => {
          try {
              // Estraiamo il vero testo inviato da Spring Boot
              const data = JSON.parse(event.data);
              console.log("📩 JSON Decodificato con successo:", data);

              if (data.event === "ring") {
                  setIncomingCall(true);
                  setCallStatus("RINGING");
              } else if (data.event === "terminate") {
                  setIncomingCall(false);
                  setCallStatus("LISTENING");
                  alert("Chiamata terminata o rifiutata dal sistema.");
              }
          } catch (error) {
              console.error("❌ Errore nella lettura del messaggio WebSocket:", error);
          }
      };

  socket.onerror = (error) => {
    console.error("❌ Errore dettagliato del WebSocket:", error);
  };

  socket.onclose = (event) => {
    console.warn(`⚠️ WebSocket chiuso. Codice: ${event.code}, Motivo: ${event.reason || "Nessuno specificato"}`);
  };

  return () => {
    socket.close();
  };
}, []);

    const handleAccept = () => {
        setIncomingCall(false);
        setCallStatus('TALKING');
        console.log('Call accepted. WebRTC peer connection starting...');
    };

    const handleReject = () => {
        setIncomingCall(false);
        setCallStatus('LISTENING');
        console.log('Call rejected.');
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'Arial' }}>
            <h1>Owner Home Dashboard</h1>
            <p>System Status: <strong>{callStatus}</strong></p>

            {incomingCall && (
                <div style={{ border: '2px solid red', padding: '20px', borderRadius: '8px', background: '#fff0f0' }}>
                    <h2>🚨 Drin Drin! Someone is at the gate!</h2>
                    <button onClick={handleAccept} style={{ marginRight: '10px', backgroundColor: 'green', color: 'white', padding: '10px' }}>
                        Accept Call
                    </button>
                    <button onClick={handleReject} style={{ backgroundColor: 'red', color: 'white', padding: '10px' }}>
                        Reject
                    </button>
                </div>
            )}
        </div>
    );
}