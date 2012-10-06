package com.googlecode;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * This class constructs the main UI of the application.
 * 
 * @author GauravKumar
 * @date july 2011
 */
public class UserInterface extends Frame implements ActionListener {

	private static final long serialVersionUID = 7406954383961330185L;

	private TextArea ta;
	private String fdDirectory;

	/**
	 * Initializes UI of the application
	 */
	public UserInterface() {
		Button b;
		add(ta = new TextArea("", 10, 100, TextArea.SCROLLBARS_VERTICAL_ONLY), BorderLayout.NORTH);
		ta.setEditable(false);
		add(b = new Button("Browse & Convert"), BorderLayout.WEST);
		b.addActionListener(this);
		add(b = new Button("Convert & Save"), BorderLayout.EAST);
		b.addActionListener(this);
		pack();
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/**
	 * Contains business logic of the application<br>
	 * Is invoked when any button is pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		if (ac.equals("Browse & Convert")) {
			FileDialog fd = new FileDialog(this, "Select source file to load", FileDialog.LOAD);
			// for persistent directory browsing
			if (fdDirectory != null)
				fd.setDirectory(fdDirectory);
			else
				fd.setDirectory(System.getenv("USERPROFILE") + "\\Desktop");
			fd.setVisible(true);
			if (fd.getFile() != null) {
				fdDirectory = fd.getDirectory();
				File file = new File(fd.getDirectory() + fd.getFile());
				ta.setText("Log Messages:\n");
				ta.append("\nSelected file: " + file.toString() + " with size of " + file.length() + " bytes");
				BufferedReader br = null;
				StringBuffer sbText = null;
				try {
					br = new BufferedReader(new InputStreamReader(new Base64InputStream(new FileInputStream(file), true, 0,
						new String("\r\n").getBytes())));
					String line;
					sbText = new StringBuffer();
					while ((line = br.readLine()) != null)
						sbText.append(line + "\r\n");
					// remove the last CRLN from the stringbuffer
					sbText.deleteCharAt(sbText.length() - 1);
					sbText.deleteCharAt(sbText.length() - 1);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (br != null)
						try {
							br.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
				ta.append("\nBase64 encoding finished with file date of length of " + sbText.length() + " bytes");
				/*
				 * Copy the following to the clipboard: ClipboardRDC- for detection of application friendly clipboard text,
				 * Filename, Base64 text.
				 */
				ClipboardRdc.copy("ClipboardRDC\n" + fd.getFile() + "\n" + sbText.toString());
				ta.append("\nText copied to the clipboard");
			}
		} else if (ac.equals("Convert & Save")) {
			BufferedWriter bw = null;
			try {
				ta.setText("Log Messages:\n\nFetching clipboard...");
				String text = ClipboardRdc.paste();
				if (text != null) {
					// detect whether the clipboard data is application friendly or not
					if (text.indexOf("ClipboardRDC") == -1) {
						ta.append("clipboard data doesn't seem to be valid RDC file data.");
						return;
					}
					String[] splitText = text.split("\n");
					ta.append("contains file date of length " + splitText[2].length() + " bytes");
					FileDialog fd = new FileDialog(this, "Select destination file to save", FileDialog.SAVE);
					// for persistent directory browsing
					if (fdDirectory != null)
						fd.setDirectory(fdDirectory);
					else
						fd.setDirectory(System.getenv("USERPROFILE") + "\\Desktop");
					fd.setFile(splitText[1]);
					fd.setVisible(true);
					if (fd.getFile() != null) {
						fdDirectory = fd.getDirectory();
						File file = new File(fd.getDirectory() + "\\" + fd.getFile());
						bw = new BufferedWriter(new OutputStreamWriter(new Base64OutputStream(new FileOutputStream(file),
							false, 0, new String("\r\n").getBytes())));
						bw.write(splitText[2]);
						bw.close();
						ta.append("\nBase64 decoded file written to " + file + " with a length of " + file.length()
							+ " bytes");
					}
				} else {
					ta.append("clipboard does not contain data of type string");
				}
			} catch (Exception e1) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e1.printStackTrace(pw);
				pw.close();
				ta.append("Exception occured:\n\n" + sw.toString());
				e1.printStackTrace();
			} finally {
				if (bw != null)
					try {
						bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		} else
			System.exit(0);
	}
}