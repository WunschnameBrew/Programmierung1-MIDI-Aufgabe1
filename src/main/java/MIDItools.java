public class MIDItools {

    // Berechnet den MIDI-Notenwert basierend auf Note, Oktave und optionalem # (sharp)
    public static byte getNote(char note, int octave, boolean sharp) {
        int base;
        switch (Character.toUpperCase(note)) {
            case 'C': base = 0; break;
            case 'D': base = 2; break;
            case 'E': base = 4; break;
            case 'F': base = 5; break;
            case 'G': base = 7; break;
            case 'A': base = 9; break;
            case 'B': base = 11; break;
            default: return 0;
        }

        int midiNote = (octave + 1) * 12 + base + (sharp ? 1 : 0);
        return (midiNote <= 127) ? (byte) midiNote : 0;
    }

    // Erzeugt den MIDI-Header mit vorgegebenen Hex-Werten und angehängter Geschwindigkeit (Tempo)
    public static byte[] getHeader(byte speed) {
        return new byte[] {
                0x4D, 0x54, 0x68, 0x64,             // "MThd" Headerkennung
                0x00, 0x00, 0x00, 0x06,             // Header-Länge = 6
                0x00, 0x00,                         // Format-Typ = 0
                0x00, 0x01,                         // Anzahl der Tracks = 1
                0x00, speed                         // Ticks per Quarter Note = speed
        };
    }

    // Erzeugt ein MIDI-Note-On oder Note-Off Event (immer 4 Byte lang)
    public static byte[] getNoteEvent(byte delay, boolean noteOn, byte note, byte velocity) {
        byte status = (byte) (noteOn ? 0x90 : 0x80); // 0x90 = Note On, 0x80 = Note Off, Channel 0
        return new byte[] {
                delay,      // Zeit seit letztem Event
                status,     // Event-Typ (Note On oder Off)
                note,       // MIDI-Notenwert
                velocity    // Geschwindigkeit des Events
        };
    }

    // Fügt ein Noten-Event an ein bestehendes Track-Array an (neues Array nötig, da Arrays fix sind)
    public static byte[] addNoteToTrack(byte[] trackdata, byte[] noteEvent) {
        byte[] result = new byte[trackdata.length + noteEvent.length];
        System.arraycopy(trackdata, 0, result, 0, trackdata.length);
        System.arraycopy(noteEvent, 0, result, trackdata.length, noteEvent.length);
        return result;
    }

    // Erzeugt einen vollständigen MIDI-Track inkl. Header, Tempo, Instrument, Noten und Track-Ende
    public static byte[] getTrack(byte instrument, byte[] trackdata) {
        byte[] header = new byte[] {
                0x4D, 0x54, 0x72, 0x6B,             // "MTrk" Track-Header // Platzhalter für Tracklänge (4 Byte)
        };

        // Standard-Meta-Events (Taktart, Tempo) und Instrumentwechsel (Channel 0)
        byte[] info = new byte[] {
                0x00, (byte)0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08,               // Taktart
                0x00, (byte)0xFF, 0x51, 0x03, 0x07, (byte)0xA1, 0x20,               // Tempo (500.000 µs / Viertelnote)
                0x00, (byte)0xC0, instrument                                         // Instrument setzen
        };

        byte[] end = new byte[] {(byte)0xFF, 0x2F, 0x00}; // Track-Ende

        int totalLength = info.length + trackdata.length;

        // Länge als 4-Byte big-endian Array kodieren
        byte[] lengthBytes = new byte[] {
                (byte)((totalLength >> 24) & 0xFF),
                (byte)((totalLength >> 16) & 0xFF),
                (byte)((totalLength >> 8) & 0xFF),
                (byte)(totalLength & 0xFF)
        };

        // Gesamtes Trackarray erstellen: Header + Länge + Info + Noten + Ende
        byte[] result = new byte[header.length + lengthBytes.length + totalLength + end.length];

        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(lengthBytes, 0, result, header.length, 4);
        System.arraycopy(info, 0, result, header.length + 4, info.length);
        System.arraycopy(trackdata, 0, result, header.length + 4 + info.length, trackdata.length);
        System.arraycopy(end, 0, result, header.length + 4 + info.length + trackdata.length, end.length);

        return result;
    }
}
