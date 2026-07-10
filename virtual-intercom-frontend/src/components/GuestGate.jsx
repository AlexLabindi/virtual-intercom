import React, { useState } from 'react';

function GuestGate({ callStatus, sessionId, setSessionId, setCallStatus, messages, onSendMessage, host }) {
    const [typedMsg, setTypedMsg] = useState('');

    const handleRing = async () => {
        try {
            const response = await fetch(`http://${host}:8090/api/intercom/ring`, { method: 'POST' });
            if (response.ok) {
                const session = await response.json();
                setSessionId(session.id || session.sessionId);
                setCallStatus('RINGING');
            }
        } catch (err) {
            console.error("Errore ring:", err);
        }
    };

    const handleCancel = async () => {
        if (!sessionId) return;
        try {
            await fetch(`http://${host}:8090/api/intercom/calls/${sessionId}/terminate?reason=CANCELED`, { method: 'POST' });
        } catch (err) {
            console.error("Errore chiusura:", err);
        }
    };

    const send = (e) => {
        e.preventDefault();
        if (!typedMsg.trim()) return;
        onSendMessage(typedMsg);
        setTypedMsg('');
    };

    return (
        <div style={{ border: '3px solid #ff9800', padding: '25px', borderRadius: '12px', width: '380px', background: '#222', boxShadow: '0 8px 20px rgba(255, 152, 0, 0.15)' }}>
            <h2 style={{ color: '#ff9800', textAlign: 'center', borderBottom: '1px solid #444', paddingBottom: '10px', marginTop: 0 }}>
                🔔 Smart Gate (Ospite)
            </h2>

            {/* BLOCCO 1: LISTENING - Solo il pulsante Suona esiste */}
            {callStatus === 'LISTENING' && (
                <div style={{ textAlign: 'center', padding: '30px 0' }}>
                    <p style={{ color: '#aaa', marginBottom: '30px' }}>Tocca il pulsante per chiamare il proprietario.</p>
                    <button
                        onClick={handleRing}
                        style={{ backgroundColor: '#ff9800', color: '#000', fontSize: '24px', fontWeight: 'bold', border: 'none', borderRadius: '50%', cursor: 'pointer', width: '130px', height: '130px', boxShadow: '0 6px 15px rgba(0,0,0,0.5)', transition: 'transform 0.1s' }}
                        onMouseDown={(e) => e.target.style.transform = 'scale(0.95)'}
                        onMouseUp={(e) => e.target.style.transform = 'scale(1)'}
                    >
                        RING
                    </button>
                </div>
            )}

            {/* BLOCCO 2: RINGING - Solo attesa e tasto per annullare */}
            {callStatus === 'RINGING' && (
                <div style={{ textAlign: 'center', padding: '20px 0' }}>
                    <div style={{ fontSize: '40px', animation: 'pulse 1.5s infinite' }}>🛎️</div>
                    <p style={{ color: '#ff9800', fontSize: '18px', fontWeight: 'bold' }}>Chiamata in corso...</p>
                    <p style={{ color: '#aaa', fontSize: '14px' }}>In attesa di risposta dal proprietario.</p>
                    <button onClick={handleCancel} style={{ backgroundColor: '#d32f2f', color: 'white', padding: '12px', border: 'none', borderRadius: '6px', cursor: 'pointer', width: '100%', marginTop: '20px', fontWeight: 'bold' }}>
                        Annulla Chiamata
                    </button>
                </div>
            )}

            {/* BLOCCO 3: CONNECTED - Finestra di Chat */}
            {callStatus === 'CONNECTED' && (
                <div style={{ marginTop: '10px' }}>
                    <div style={{ backgroundColor: '#111', color: '#00e676', padding: '8px', textAlign: 'center', borderRadius: '4px', marginBottom: '10px', fontSize: '14px', fontWeight: 'bold' }}>
                        🟢 Comunicazione Aperta
                    </div>

                    <div style={{ height: '200px', overflowY: 'auto', border: '1px solid #444', padding: '10px', background: '#1a1a1a', borderRadius: '6px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        {messages.map((m, i) => (
                            <div key={i} style={{ alignSelf: m.sender === 'SMART' ? 'flex-end' : 'flex-start', maxWidth: '80%' }}>
                                <span style={{ fontSize: '11px', color: '#888', display: 'block', marginBottom: '2px' }}>{m.sender}</span>
                                <div style={{ backgroundColor: m.sender === 'SMART' ? '#ff9800' : '#333', color: m.sender === 'SMART' ? '#000' : '#fff', padding: '8px 12px', borderRadius: '12px', borderBottomRightRadius: m.sender === 'SMART' ? '0' : '12px', borderBottomLeftRadius: m.sender === 'SMART' ? '12px' : '0' }}>
                                    {m.text}
                                </div>
                            </div>
                        ))}
                    </div>

                    <form onSubmit={send} style={{ display: 'flex', marginTop: '15px', gap: '8px' }}>
                        <input type="text" value={typedMsg} onChange={(e) => setTypedMsg(e.target.value)} style={{ flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #555', background: '#333', color: '#fff' }} placeholder="Scrivi un messaggio..." />
                        <button type="submit" style={{ backgroundColor: '#ff9800', color: '#000', fontWeight: 'bold', padding: '10px 20px', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Invia</button>
                    </form>

                    <button onClick={handleCancel} style={{ backgroundColor: '#d32f2f', color: 'white', padding: '10px', border: 'none', borderRadius: '6px', cursor: 'pointer', width: '100%', marginTop: '15px', fontWeight: 'bold' }}>
                        Riaggancia
                    </button>
                </div>
            )}
        </div>
    );
}

export default GuestGate;