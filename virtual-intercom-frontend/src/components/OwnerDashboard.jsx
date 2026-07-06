import React, { useState, useEffect } from 'react';

/**
 * OwnerDashboard Component - Main monitoring hub open on the owner's home PC browser.
 * Listens to real-time ring notifications via WebSocket.
 */
export default function OwnerDashboard() {
    const [incomingCall, setIncomingCall] = useState(false);
    const [callStatus, setCallStatus] = useState('LISTENING'); // LISTENING, TALKING

    useEffect(() => {
        // Establish native WebSocket connection with the Spring Boot signaling server
        const socket = new WebSocket('ws://localhost:8090/ws/signaling');

        socket.onmessage = (event) => {
            console.log('Real-time signal received:', event.data);
            // Simulating an incoming ring event from the gate
            setIncomingCall(true);
        };

        return () => socket.close();
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