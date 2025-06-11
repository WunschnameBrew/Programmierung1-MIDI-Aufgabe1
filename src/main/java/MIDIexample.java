import java.io.*;

public class MIDIexample {
    public static void writeData(byte[] header, byte[] track, String filename) {
        try (FileOutputStream stream = new FileOutputStream(new File(filename))) {
            stream.write(header);
            stream.write(track);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot write to file " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file " + filename);
        }
    }

    public static void main(String[] args) {
        char[] notes = new char[] {'C', 'D', 'E', 'F', 'G' ,'G'};
        byte[] trackdata = new byte[0];
        for(char note : notes) {
            trackdata = MIDItools.addNoteToTrack(trackdata, MIDItools.getNoteEvent((byte)60, true, MIDItools.getNote(note, 2, false), (byte)0x60));
        }
        writeData(MIDItools.getHeader((byte)40), MIDItools.getTrack((byte)0x05, trackdata), "test.midi");
    }
}
