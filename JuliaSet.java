import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Hashtable;

public class JuliaSet extends JPanel implements ChangeListener, ActionListener {
	private static final long serialVersionUID = -1134091646286961749L;

	private JFrame frame;
	private JDialog controlDialog, presetDialog;
	private JPanel controlPanel, labelPanel, sliderPanel, inputPanel, buttonPanel, presetPanel;
	private JSlider aSlider, bSlider, hueSlider, hueVariationSlider, brightnessSlider, maxIterationsSlider, zoomSlider;
	private JTextField aInput, bInput, hueInput, hueVariationInput, brightnessInput, maxIterationsInput, zoomInput;
	private JButton saveButton, presetButton;

	private BufferedImage image;
	private int width = 1000, height = width * 2 / 3;
	private double a = 0, b = 0; //3 decimal places
	private float hue = 1, hueVariation = 1, brightness = 1;
	private float maxIterations = 200;
	private double zoom = 0.75;

	public JuliaSet() {
		frame = new JFrame("Julia Set");
		frame.add(this);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(1, 7));
		labelPanel.add(new JLabel("A", JLabel.CENTER));
		labelPanel.add(new JLabel("B", JLabel.CENTER));
		labelPanel.add(new JLabel("Hue", JLabel.CENTER));
		labelPanel.add(new JLabel("<html><p style=\"text-align: center; font-size:8px\">Hue Variations</p></html>", JLabel.CENTER));
		labelPanel.add(new JLabel("<html><p style=\"text-align: center; font-size:8px\">Brightness</p></html>", JLabel.CENTER));
		labelPanel.add(new JLabel("<html><p style=\"text-align: center; font-size:8px\">Max Iterations</p></html>", JLabel.CENTER));
		labelPanel.add(new JLabel("Zoom", JLabel.CENTER));
		
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(1, 7));
		sliderPanel.add(aSlider = getSlider(-2000, 2000, (int)(a * 1000), 1000, getSliderLabels(-2000, 2000, 1000)));
		sliderPanel.add(bSlider = getSlider(-2000, 2000, (int)(b * 1000), 1000, getSliderLabels(-2000, 2000, 1000)));
		sliderPanel.add(hueSlider = getSlider(0, 1000, (int)(hue * 1000), 250, getSliderLabels(0, 1000, 250)));
		sliderPanel.add(hueVariationSlider = getSlider(0, 2000, (int)(hueVariation * 1000), 500, getSliderLabels(0, 2000, 500)));
		sliderPanel.add(brightnessSlider = getSlider(0, 2000, (int)(brightness * 1000), 500, getSliderLabels(0, 2000, 500)));
		sliderPanel.add(maxIterationsSlider = getSlider(0, 1000, (int)(maxIterations), 250, null));
		sliderPanel.add(zoomSlider = getSlider(0, 10000, (int)(zoom * 1000), 2500, getSliderLabels(0, 10000, 2500)));

		inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 7));
		inputPanel.add(aInput = getTextField(String.valueOf(a)));
		inputPanel.add(bInput = getTextField(String.valueOf(b)));
		inputPanel.add(hueInput = getTextField(String.valueOf(hue)));
		inputPanel.add(hueVariationInput = getTextField(String.valueOf(hueVariation)));
		inputPanel.add(brightnessInput = getTextField(String.valueOf(brightness)));
		inputPanel.add(maxIterationsInput = getTextField(String.valueOf(maxIterations)));
		inputPanel.add(zoomInput = getTextField(String.valueOf(zoom)));

		saveButton = new JButton("Save Image");
		saveButton.addActionListener((e) -> {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg or png", "jpg", "png");
			fileChooser.setFileFilter(filter);
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				String ext = file.getName().substring(file.getName().indexOf(".") + 1);
				if (!ext.matches("jpg|png")) {
					JOptionPane.showMessageDialog(frame, "Only jpg and png file types are allowed.");
				}
				if (file.exists()) {
					int response = JOptionPane.showConfirmDialog(frame, "Do you want to replace the selected file?", "File Already Exists", JOptionPane.WARNING_MESSAGE);
					if (response == JOptionPane.CANCEL_OPTION) return;
				}
				try {
					ImageIO.write(image, ext, file);
				} catch (IOException ex) {System.out.println("error");}
			}
		});

		presetDialog = new JDialog();
		presetDialog.setTitle("Presets");
		presetDialog.setSize(440, 200);
		presetDialog.setLocation(frame.getX() + frame.getWidth(), frame.getY() + frame.getHeight());

		presetPanel = new JPanel();
		presetPanel.setLayout(new GridLayout(4, 2));
		presetPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		presetPanel.add(makePresetButton("Preset 1", -0.855, 0.23, 0.58f, 0.835f, 1.002f, 750f, 0.956));
		presetPanel.add(makePresetButton("Preset 2", 0.338, -0.048, 0.777f, 0.93f, 1.26f, 430f, 0.83));
		presetPanel.add(makePresetButton("Preset 3", -0.382, 0.607, 0.655f, 1.3f, 0.95f, 375f, 1.05));
		presetPanel.add(makePresetButton("Preset 4", -0.045, -0.679, 0.625f, 1.905f, 1f, 370f, 0.972));
		presetPanel.add(makePresetButton("Preset 5", -1.23, -0.12, 1.0f, 0.16f, 1.002f, 500f, 2.995));
		presetPanel.add(makePresetButton("Preset 6", -0.404, -0.588, 0.78f, 0.65f, 1f, 190f, 0.925));
		presetPanel.add(makePresetButton("Preset 7", -0.307, -0.629, 0.7f, 2f, 1f, 1000f, 0.916));
		presetPanel.add(makePresetButton("Reset", 0, 0, 1, 1, 1, 250, 0.75));
		presetDialog.getContentPane().add(presetPanel);

		presetButton = new JButton("Load Preset...");
		presetButton.addActionListener((e) -> presetDialog.setVisible(true));

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(presetButton);
		buttonPanel.add(saveButton);

		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		controlPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
		controlPanel.add(sliderPanel, BorderLayout.CENTER);
		controlPanel.add(labelPanel, BorderLayout.NORTH);
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(2, 1));
		container.add(inputPanel);
		container.add(buttonPanel);
		controlPanel.add(container, BorderLayout.SOUTH);

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		redraw();

		frame.setVisible(true);

		controlDialog = new JDialog();
		controlDialog.getContentPane().add(controlPanel);
		controlDialog.setSize(presetDialog.getWidth(), height);
		controlDialog.setLocation(frame.getX() + frame.getWidth(), frame.getY());
		controlDialog.setVisible(true);
	}

	public void redraw() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float i = maxIterations;
				double zx = 1.5 * ((x - width / 2) / (zoom / 2 * width));
				double zy = (y - height / 2) / (zoom / 2 * height);
				while ((zx * zx) + (zy * zy) < 6 && i > 0) {
					double temp = (zx * zx) - (zy * zy) + a;
					zy = 2 * zx * zy + b;
					zx = temp;
					i--;
				}
				int c = (i > 0) ? Color.HSBtoRGB(hueVariation * ((maxIterations / i) % hue), 1, brightness) : Color.HSBtoRGB(maxIterations / i, 1, 0);
				image.setRGB(x, y, c);
			}
		}
		repaint();
	}

	public JTextField getTextField(String text) {
		JTextField textField = new JTextField(text);
		textField.addActionListener(this);
		return textField;
	}

	public JSlider getSlider(int min, int max, int start, int majorSpacing, Hashtable<Integer, JLabel> labelTable) {
		JSlider slider = new JSlider(JSlider.VERTICAL, min, max, start);
		slider.setMajorTickSpacing(majorSpacing);
		slider.setPaintTicks(true);
		if (labelTable != null) slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		return slider;
	}

	public Hashtable<Integer, JLabel> getSliderLabels(int min, int max, int spacing) {
		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		for (int tick = min; tick <= max; tick += spacing) {
			labels.put(tick, new JLabel(String.valueOf(tick / 1000.0)));
		}
		return labels;
	}

	public JButton makePresetButton(String name, double a, double b, float hue, float hueVariation, float brightness, float maxIterations, double zoom) {
		JButton button = new JButton(name);
		button.addActionListener((e) -> loadPreset(a, b, hue, hueVariation, brightness, maxIterations, zoom));
		return button;
	}

	public void loadPreset(double a, double b, float hue, float hueVariation, float brightness, float maxIterations, double zoom) {
		aInput.setText(String.valueOf(this.a = a));
		aSlider.setValue((int)(a * 1000));
		bInput.setText(String.valueOf(this.b = b));
		bSlider.setValue((int)(b * 1000));
		hueInput.setText(String.valueOf(this.hue = hue));
		hueSlider.setValue((int)(hue * 1000));
		hueVariationInput.setText(String.valueOf(this.hueVariation = hueVariation));
		hueVariationSlider.setValue((int)(hueVariation * 1000));
		brightnessInput.setText(String.valueOf(this.brightness = brightness));
		brightnessSlider.setValue((int)(brightness * 1000));
		maxIterationsInput.setText(String.valueOf(this.maxIterations = maxIterations));
		maxIterationsSlider.setValue((int)(maxIterations));
		zoomInput.setText(String.valueOf(this.zoom = zoom));
		zoomSlider.setValue((int)(zoom * 1000));
		redraw();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if (src == aSlider) aInput.setText(String.valueOf(a = aSlider.getValue() / 1000.0));
		else if (src == bSlider) bInput.setText(String.valueOf(b = bSlider.getValue() / 1000.0));
		else if (src == hueSlider) hueInput.setText(String.valueOf(hue = hueSlider.getValue() / 1000.0f));
		else if (src == hueVariationSlider) hueVariationInput.setText(String.valueOf(hueVariation = hueVariationSlider.getValue() / 1000.0f));
		else if (src == brightnessSlider) brightnessInput.setText(String.valueOf(brightness = brightnessSlider.getValue() / 1000.0f));
		else if (src == maxIterationsSlider) maxIterationsInput.setText(String.valueOf(maxIterations = maxIterationsSlider.getValue()));
		else if (src == zoomSlider) zoomInput.setText(String.valueOf(zoom = zoomSlider.getValue() / 1000.0));
		redraw();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		try {
			if (src == aInput) aSlider.setValue((int)((a = Double.parseDouble(aInput.getText())) * 1000));
			else if (src == bInput) bSlider.setValue((int)((b = Double.parseDouble(bInput.getText())) * 1000));
			else if (src == hueInput) hueSlider.setValue((int)(hue = Float.parseFloat(hueInput.getText()) * 1000));
			else if (src == hueVariationInput) hueVariationSlider.setValue((int)(hueVariation = Float.parseFloat(hueVariationInput.getText()) * 1000));
			else if (src == brightnessInput) brightnessSlider.setValue((int)(brightness = Float.parseFloat(brightnessInput.getText()) * 1000));
			else if (src == maxIterationsInput) maxIterationsSlider.setValue((int)(maxIterations = Float.parseFloat(maxIterationsInput.getText())));
			else if (src == zoomInput) zoomSlider.setValue((int)(zoom = Double.parseDouble(zoomInput.getText()) * 1000));
			((JTextField)src).setForeground(Color.BLACK);
		} catch (NumberFormatException ex) {
			((JTextField)src).setForeground(Color.RED);
		}
		redraw();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	public static void main(String[] args) {
		new JuliaSet();
	}
}