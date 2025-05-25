export default function MeetingsList({meetings, onDelete, onAdd}) {
    return (
        <table>
            <thead>
            <tr>
                <th>Nazwa spotkania</th>
                <th>Opis</th>
                <th>Usuń</th>
                <th>Dołącz</th>
                <th>Opuść</th>
            </tr>
            </thead>
            <tbody>
            {
                meetings.map((meeting, index) => <tr key={index}>
                    <td>{meeting.title}</td>
                    <td>{meeting.description}</td>
                    <td>
                    <button type="button" onClick={() => onDelete(meeting)}> Usuń </button>
                    </td>
                    <td>
                        <button type="button" onClick={() => onAdd(meeting)}> Dołącz </button>
                    </td>
                    <td>
                        <button type="button" onClick={() => onDelete(meeting)}> Opuść </button>
                    </td>
                </tr>)
            }
            </tbody>
        </table>
    );
}
