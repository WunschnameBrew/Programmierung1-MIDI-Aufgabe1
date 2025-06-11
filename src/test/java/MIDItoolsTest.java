import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MIDItoolsTest {

    @Test
    void getHeader() {
        assertArrayEquals(new byte[] {0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, 0x00, 0x60}, MIDItools.getHeader((byte)0x60));
        assertArrayEquals(new byte[] {0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, 0x00, 0x10}, MIDItools.getHeader((byte)0x10));
    }

    @Test
    void getNote() {
        assertEquals(0, MIDItools.getNote('C', -1, false));
        assertEquals(66, MIDItools.getNote('F', 4, true));
        assertEquals(120, MIDItools.getNote('B', 8, true));
        assertEquals(105, MIDItools.getNote('A', 7, false));
        assertEquals(0, MIDItools.getNote('7', 1, true));
        assertEquals(0, MIDItools.getNote('A', 11, false));
    }

    @Test
    void getNoteEvent() {
        assertArrayEquals(new byte[] {(byte)0x30, (byte)0b10010000, (byte)12, (byte)120}, MIDItools.getNoteEvent((byte)0x30, true, (byte)12, (byte)120));
        assertArrayEquals(new byte[] {(byte)0x60, (byte)0b10000000, (byte)15, (byte)100}, MIDItools.getNoteEvent((byte)0x60, false, (byte)15, (byte)100));
    }

    @Test
    void addNoteToTrack() {
        assertArrayEquals(
                new byte[] {0x00, 0x23, 0x22, 0x20},
                MIDItools.addNoteToTrack(new byte[0], new byte[] {0x00, 0x23, 0x22, 0x20})
        );
        assertArrayEquals(
                new byte[] {0x00, 0x23, 0x22, 0x20, 0x60, 0x11, 0x12, 0x66},
                MIDItools.addNoteToTrack(new byte[] {0x00, 0x23, 0x22, 0x20}, new byte[] {0x60, 0x11, 0x12, 0x66})
        );
    }

    @Test
    void getTrack() {
        assertArrayEquals(new byte[] {0x4D, 0x54, 0x72, 0x6B, 0x00, 0x00, 0x00, (byte)(22),
                0x00, (byte)0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08, 0x00, (byte)0xFF, 0x51, 0x03, 0x07, (byte)0xA1, 0x20,
                0x00, (byte)0xC0, 0x05, (byte)0x30, (byte)0b10010000, (byte)12, (byte)120, (byte)0xFF, 0x2F, 0x00},
                MIDItools.getTrack((byte)0x05, new byte[] {(byte)0x30, (byte)0b10010000, (byte)12, (byte)120}));
    }
}