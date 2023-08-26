package eu.jodelahithit;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Dictionary;
import java.util.Hashtable;

public class SkillingNotificationsPanel extends PluginPanel {
    private final Dictionary<String, BufferedImage> iconsCache = new Hashtable<>();
    private final ConfigManager configManager;
    private final JPanel skillsPanel, enabledPanel, flashingPanel, walkingPanel;

    @Inject
    SkillingNotificationsPanel(ConfigManager configManager) {
        super();
        this.configManager = configManager;
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 10, 0);

        final JLabel welcomeText = new JLabel("Skilling Notifications");
        welcomeText.setFont(FontManager.getRunescapeBoldFont());
        welcomeText.setHorizontalAlignment(JLabel.CENTER);

        skillsPanel = new JPanel();
        skillsPanel.setLayout(new GridLayout(0, 2 , 7, 7));

        enabledPanel = new JPanel();
        enabledPanel.setLayout(new GridLayout(1, 1, 0, 0));
        flashingPanel = new JPanel();
        flashingPanel.setLayout(new GridLayout(1, 1, 0, 0));
        walkingPanel = new JPanel();
        walkingPanel.setLayout(new GridLayout(1, 1, 0, 0));


        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        descriptionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea description = new JTextArea(0, 25);
        description.setText("This plugin will display an overlay when the player is not actively performing any of the following selected skills.\n\nExtra notification delays can be configured in the plugin configuration.");
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setOpaque(false);
        description.setEditable(false);
        description.setFocusable(false);
        description.setBackground(ColorScheme.DARK_GRAY_COLOR);
        description.setFont(FontManager.getRunescapeSmallFont());
        description.setBorder(new EmptyBorder(0, 0, 0, 0));

        descriptionPanel.add(description);

        repaintConfigButtons();

        add(welcomeText, c);
        c.gridy++;
        add(descriptionPanel, c);
        c.gridy++;
        add(enabledPanel, c);
        c.gridy++;
        add(skillsPanel, c);
        c.gridy++;
        add(walkingPanel, c);
        c.gridy++;
        add(flashingPanel, c);
    }

    @Override
    public void onActivate() {
        repaintConfigButtons();
    }

    public void repaintConfigButtons() {
        setVisible(false);
        skillsPanel.removeAll();
        for (Skill skill : Skill.values()) {
            if (skill == Skill.NONE) continue;
            String skillIcon = "/skill_icons/" + skill.name().toLowerCase() + ".png";
            ImageIcon icon = new ImageIcon(GetIcon(skill.customImage == null ? skillIcon : skill.customImage));
            boolean isActive = Boolean.parseBoolean(configManager.getConfiguration("Skilling Notifications", skill.name()));
            JToggleButton toggleButton = new JToggleButton(icon, isActive);
            toggleButton.setToolTipText(StringUtils.capitalize(skill.name().toLowerCase()));
            toggleButton.setFocusable(false);
            toggleButton.addItemListener(ev -> configManager.setConfiguration("Skilling Notifications", skill.name(), ev.getStateChange() == ItemEvent.SELECTED));
            skillsPanel.add(toggleButton);
        }

        enabledPanel.removeAll();
        JToggleButton enabledButton = new JToggleButton("Enabled", Boolean.parseBoolean(configManager.getConfiguration("Skilling Notifications", "enabled")));
        enabledButton.setFocusable(false);
        enabledButton.setToolTipText("Toggles the overlay and plugin functionality");
        enabledButton.addItemListener(ev -> configManager.setConfiguration("Skilling Notifications", "enabled", ev.getStateChange() == ItemEvent.SELECTED));
        enabledPanel.add(enabledButton);

        walkingPanel.removeAll();
        JToggleButton walkingButton = new JToggleButton("Disable overlay while walking", Boolean.parseBoolean(configManager.getConfiguration("Skilling Notifications", "disableWhenWalking")));
        walkingButton.setFocusable(false);
        walkingButton.setToolTipText("Forces the notification overlay to be disabled while walking or running");
        walkingButton.addItemListener(ev -> configManager.setConfiguration("Skilling Notifications", "disableWhenWalking", ev.getStateChange() == ItemEvent.SELECTED));
        walkingPanel.add(walkingButton);

        flashingPanel.removeAll();
        JToggleButton flashingButton = new JToggleButton("Notification flash", Boolean.parseBoolean(configManager.getConfiguration("Skilling Notifications", "notificationFlash")));
        flashingButton.setFocusable(false);
        flashingButton.setToolTipText("Flashes notifications at the configured interval");
        flashingButton.addItemListener(ev -> configManager.setConfiguration("Skilling Notifications", "notificationFlash", ev.getStateChange() == ItemEvent.SELECTED));
        flashingPanel.add(flashingButton);
        setVisible(true);
    }

    private BufferedImage GetIcon(String path) {
        BufferedImage iconImage = iconsCache.get(path);
        if (iconImage != null) return iconImage;
        iconImage = ImageUtil.loadImageResource(getClass(), path);
        iconsCache.put(path, iconImage);
        return iconImage;
    }
}
