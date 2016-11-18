/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleapdu;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xsvenda
 */
public class DirtyLogger {
    FileOutputStream    m_logFile;
    boolean             m_bOutputSystemOut = true;
    public DirtyLogger(FileOutputStream logFile, boolean bOutputSystemOut) {
        m_logFile = logFile;
        m_bOutputSystemOut = bOutputSystemOut;
    }
    public void println() {
        String logLine = "\n";
        print(logLine);
    }
    public void println(String logLine)  {
        logLine += "\n";
        print(logLine);
    }
    public void print(String logLine) {
        if (m_bOutputSystemOut) {
            System.out.print(logLine);
        }
        if (m_logFile != null) {
            try {
                m_logFile.write(logLine.getBytes());
            } catch (IOException ex) {
            }
        }
    }    

    void flush() {
        try {
            m_logFile.flush();
        } catch (IOException ex) {
        }
    }
}
