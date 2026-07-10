import React, { useState } from 'react';

function OwnerDashboard({ callStatus, sessionId, setSessionId, setCallStatus, messages, onSendMessage, host }) {
    const [typedMsg, setTypedMsg] = useState('');

    const handleAccept = async () => {
        console.log("🔘 Click su ACCETTA. ID Sessione attuale:", sessionId);
        if (!sessionId) {
            console.error("❌ Impossibile accettare: sessionId è NULL o UNDEFINED!");
            return;
        }
        try {
            const response = await fetch(`http://${host}:8090/api/intercom/calls/${sessionId}/accept`, { method: 'POST' });
            if (response.ok) {
                setCallStatus('CONNECTED');
            } else {
                console.error("❌ Il server ha rifiutato la richiesta di accept:", response.status);
            }
        } catch (err) {
            console.error("Errore di rete durante accept:", err);
        }
    };

    const handleReject = async () => {
        console.log("🔘 Click su RIFIUTA. ID Sessione attuale:", sessionId);
        if (!sessionId) {
            console.error("❌ Impossibile rifiutare: sessionId è NULL o UNDEFINED!");
            return;
        }
        try {
            const response = await fetch(`http://${host}:8090/api/intercom/calls/${sessionId}/terminate?reason=REJECTED`, { method: 'POST' });
            if (response.ok) {
                setCallStatus('LISTENING');
                setSessionId(null);
            } else {
                console.error("❌ Il server ha rifiutato la richiesta di terminate:", response.status);
            }
        } catch (err) {
            console.error("Errore di rete durante terminate:", err);
        }
    };

    const send = (e) => {
        e.preventDefault();
        if (!typedMsg.trim()) return;
        onSendMessage(typedMsg);
        setTypedMsg('');
    };

    return (
        <div style={{ border: '3px solid #4CAF50', padding: '25px', borderRadius: '12px', width: '400px', background: '#1e1e1e', boxShadow: '0 8px 20px rgba(76, 175, 80, 0.15)' }}>
            <h2 style={{ color: '#4CAF50', textAlign: 'center', borderBottom: '1px solid #444', paddingBottom: '10px', marginTop: 0 }}>
                🏠 Dashboard Proprietario
            </h2>

            {/* BLOCCO 1: LISTENING - Standby */}
            {callStatus === 'LISTENING' && (
                <div style={{ textAlign: 'center', padding: '40px 0', color: '#888' }}>
                    <div style={{ fontSize: '50px', marginBottom: '15px' }}>🛡️</div>
                    <h3 style={{ margin: 0, color: '#aaa' }}>Sistema in Standby</h3>
                    <p style={{ fontSize: '14px' }}>Nessuna chiamata dal cancello.</p>
                </div>
            )}

            {/* BLOCCO 2: RINGING - Schermata di controllo della chiamata */}
            {callStatus === 'RINGING' && (
                <div style={{ textAlign: 'center', padding: '20px 0' }}>
                    <div style={{ fontSize: '40px' }}>📱</div>
                    <h3 style={{ color: '#fff', marginTop: '10px' }}>Qualcuno è al cancello!</h3>
                    <p style={{ fontSize: '11px', color: '#666' }}>ID: {sessionId || "Mancante"}</p>
                    <div style={{ display: 'flex', gap: '15px', marginTop: '25px' }}>
                        <button onClick={handleAccept} style={{ flex: 1, backgroundColor: '#4CAF50', color: 'white', fontSize: '16px', fontWeight: 'bold', padding: '15px', border: 'none', borderRadius: '6px', cursor: 'pointer', boxShadow: '0 4px 6px rgba(0,0,0,0.3)' }}>
                            ✔️ ACCETTA
                        </button>
                        <button onClick={handleReject} style={{ flex: 1, backgroundColor: '#d32f2f', color: 'white', fontSize: '16px', fontWeight: 'bold', padding: '15px', border: 'none', borderRadius: '6px', cursor: 'pointer', boxShadow: '0 4px 6px rgba(0,0,0,0.3)' }}>
                            ❌ RIFIUTA
                        </button>
                    </div>
                </div>
            )}

            {/* BLOCCO 3: CONNECTED - Interfaccia Chat */}
            {callStatus === 'CONNECTED' && (
                <div style={{ marginTop: '10px' }}>
                    <div style={{ backgroundColor: '#111', color: '#00e676', padding: '8px', textAlign: 'center', borderRadius: '4px', marginBottom: '10px', fontSize: '14px', fontWeight: 'bold' }}>
                        🟢 Connessione Stabilita
                    </div>

                    <div style={{ height: '200px', overflowY: 'auto', border: '1px solid #444', padding: '10px', background: '#1a1a1a', borderRadius: '6px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        {messages.map((m, i) => (
                            <div key={i} style={{ alignSelf: m.sender === 'OWNER' ? 'flex-end' : 'flex-start', maxWidth: '80%' }}>
                                <span style={{ fontSize: '11px', color: '#888', display: 'block', marginBottom: '2px', textAlign: m.sender === 'OWNER' ? 'right' : 'left' }}>{m.sender}</span>
                                <div style={{ backgroundColor: m.sender === 'OWNER' ? '#4CAF50' : '#333', color: '#fff', padding: '8px 12px', borderRadius: '12px', borderBottomRightRadius: m.sender === 'OWNER' ? '0' : '12px', borderBottomLeftRadius: m.sender === 'OWNER' ? '12px' : '0' }}>
                                    {m.text}
                                </div>
                            </div>
                        ))}
                    </div>

                    <form onSubmit={send} style={{ display: 'flex', marginTop: '15px', gap: '8px' }}>
                        <input type="text" value={typedMsg} onChange={(e) => setTypedMsg(e.target.value)} style={{ flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #555', background: '#333', color: '#fff' }} placeholder="Rispondi all'ospite..." />
                        <button type="submit" style={{ backgroundColor: '#4CAF50', color: 'white', fontWeight: 'bold', padding: '10px 20px', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Invia</button>
                    </form>

                    <button onClick={handleReject} style={{ backgroundColor: '#d32f2f', color: 'white', padding: '10px', border: 'none', borderRadius: '6px', cursor: 'pointer', width: '100%', marginTop: '15px', fontWeight: 'bold' }}>
                        Termina Chiamata (Forza Chiusura)
                    </button>
                </div>
            )}
        </div>
    );
}

export default OwnerDashboard;