import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
public class VisaApplicationForm {
    private JSONArray successfulSubmissions;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VisaApplicationForm().createAndShowGUI();
        });
    }
    public VisaApplicationForm() {
        successfulSubmissions = new JSONArray();
        loadSubmissionsFromFile();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Visa Application Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.setBorder(new EmptyBorder(20,20,20,20));
        container.add(formPanel, BorderLayout.CENTER);

        JLabel visaTypeLabel = new JLabel("Visa Type:");
        JComboBox<String> visaTypeComboBox = new JComboBox<>(new String[] {"Work Visa", "Student Visa", "Citizenship"});
        formPanel.add(visaTypeLabel);
        formPanel.add(visaTypeComboBox);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(30);
        formPanel.add(nameLabel);
        formPanel.add(nameField);

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        JTextField dobField = new JTextField(10);
        formPanel.add(dobLabel);
        formPanel.add(dobField);

        JLabel passportLabel = new JLabel("Passport Number:");
        JTextField passportField = new JTextField(9);
        formPanel.add(passportLabel);
        formPanel.add(passportField);

        JButton submitButton = new JButton("Submit");
        container.add(submitButton, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String dob = dobField.getText();
                String passport = passportField.getText();
                String visaType = (String) visaTypeComboBox.getSelectedItem();
                Random random = new Random();
                int randomNumber = 100000000 + random.nextInt(900000000);
                String trackingNumber = Integer.toString(randomNumber);


                if (!validateName(name)) {
                    JOptionPane.showMessageDialog(frame, "Error: Name entered is invalid. Please check that your name is not empty and that no special characters are used and try again.");
                    return;
                }
                if (!validateDob(dob))
                {
                    JOptionPane.showMessageDialog(frame, "Error: Date of Birth is invalid. Please check your information and try again.");
                    return;
                }
                if (!validatePassport(passport)) {
                    JOptionPane.showMessageDialog(frame, "Error: Passport number is invalid. Please check your information and try again.");
                    return;
                }
                if (passportNumberExists(passport, visaType)) {
                    JOptionPane.showMessageDialog(frame, "Error: Passport number already exists for this application type. Please check your information and try again.");
                    return;
                }
                if (validateName(name) && validateDob(dob) && validatePassport(passport)) {
                    while (trackingNumberExists(trackingNumber)) {
                        randomNumber = 100000000 + random.nextInt(900000000);
                        trackingNumber = Integer.toString(randomNumber);
                    }


                    JSONObject submission = new JSONObject();
                    submission.put("name", name);
                    submission.put("dob", dob);
                    submission.put("passport", passport);
                    submission.put("tracking number", trackingNumber);
                    submission.put("visaType", visaType);

                    successfulSubmissions.put(submission);
                    saveSubmissionsToFile();

                    JOptionPane.showMessageDialog(frame, "Visa application submitted successfully!\nYour tracking number is: "+trackingNumber);

                } else {
                    JOptionPane.showMessageDialog(frame, "Error: Invalid input. Please check your information and try again.");
                }
            }
        });

        frame.setVisible(true);
    }

    private boolean validateName(String name) {
        String[] words = name.split("\\s+");
        StringBuilder sb = new StringBuilder();
        boolean b;
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
            }
            sb.append(" ");
        }
        b = (!sb.toString().trim().isEmpty() && sb.toString().trim().matches("[a-zA-Z ]*"));
        return b;
    }

    private boolean validateDob(String dob) {
        return dob.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean validatePassport(String passport) {
        return passport.matches("\\d{9}");
    }

    private void saveSubmissionsToFile() {
        try (FileWriter fileWriter = new FileWriter("successful_submissions.json")) {
            fileWriter.append(successfulSubmissions.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSubmissionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("successful_submissions.json"))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            successfulSubmissions = new JSONArray(content.toString());
        } catch (IOException e) {
            System.out.println("Error reading successful_submissions.json file. Assuming it doesn't exist yet.");
        }
    }
    private boolean passportNumberExists(String passportNumber, String visaType) {
        for (int i = 0; i < successfulSubmissions.length(); i++) {
            JSONObject submission = successfulSubmissions.getJSONObject(i);
            if (submission.getString("passport").equals(passportNumber) && submission.getString("visaType").equals(visaType)) {
                return true;
            }
        }
        return false;
    }
    private boolean trackingNumberExists(String trackingNumber) {
        for (int i = 0; i < successfulSubmissions.length(); i++) {
            JSONObject submission = successfulSubmissions.getJSONObject(i);
            if (submission.getString("tracking number").equals(trackingNumber)) {
                return true;
            }
        }
        return false;
    }
}
