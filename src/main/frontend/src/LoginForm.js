import {useState} from "react";

export default function LoginForm({onLogin, buttonLabel}) {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);


    async function handleRegister() {
        setLoading(true);
        try {
            const response = await fetch('api/participants', {
                method: 'POST',
                body: JSON.stringify({login: email}),
                headers: {'Content-Type': 'application/json'}
            });

            await new Promise(resolve => setTimeout(resolve, 1500));

            if (!response.ok) {
                throw new Error('Błąd logowania');
            }

            onLogin(email);
        } catch (error) {
            console.error(error);
            alert("Nie udało się zalogować.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <label>Zaloguj się e-mailem</label>
            <input
                type="text"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
            />
            <button type="button" onClick={handleRegister} disabled={loading}>
                {loading ? 'Czekaj...' : (buttonLabel || 'Wchodzę')}
            </button>

            {loading && (
                <div className="lds-ring">
                    <div></div><div></div><div></div><div></div>
                </div>
            )}
        </div>
    );
}
