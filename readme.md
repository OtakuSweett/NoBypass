
# NoBypass Plugin

**NoBypass** is a Minecraft plugin designed to enhance server security by validating player connections based on allowed domains and reserved UUIDs. The plugin provides administrators with robust tools to prevent unauthorized access, ensure compliance with server policies, and integrate notifications via Discord webhooks.



## ✨ Features
- **Domain Validation**: Restrict connections to specific, pre-approved domains.
- **UUID Reservation**: Validate players by matching their UUIDs against reserved UUIDs for specific usernames.
- **Customizable Webhook Notifications**: Notify administrators of blocked connections via Discord, including detailed player information and reasons for denial.
- **Multi-language Support**: Includes support for English, Spanish, and French.
- **Debug Mode**: Provides detailed logging for troubleshooting and server management.



## ⚙️ Configuration
The plugin generates a `config.yml` file upon first use, which can be customized to suit your server's needs. Example configuration options include:
- **Allowed Domains**: List of domains allowed for connection.
- **Webhook Settings**: URL and embed customization for Discord notifications.
- **Debug Mode**: Enable or disable detailed logs.



## 🛠️ Commands and Permissions

### Commands
- `/nobypass reload`: Reloads the plugin's configuration and language files.

### Permissions
- `nobypass.reload`: Grants access to reload the plugin configuration.
- `nobypass.notify`: Grants access to receive notifications about blocked connections.



## 🚀 How It Works
1. **Connection Validation**:  
   When a player connects, the plugin checks if their domain matches one of the allowed domains. If not, they are denied access with a customizable kick message.
2. **UUID Check**:  
   If UUID reservation is enabled, the plugin ensures the player's UUID matches the reserved UUID for their username.
3. **Webhook Notifications**:  
   If a connection is blocked, administrators receive a Discord notification with details such as:
   - Player Username
   - Domain Used
   - Reason for Denial
   - Player UUID



## 📥 Installation

1. **Download**:  
   Get the latest version of the plugin from [SpigotMC](https://www.spigotmc.org/resources/nobypass-velocity.121973/).
2. **Install**:  
   Place the `NoBypass.jar` file into your server's `plugins` directory.
3. **Start the Server**:  
   Launch or restart your server to generate the configuration files.
4. **Customize**:  
   Edit the `config.yml` file to configure the plugin to your server's needs.
5. **Reload**:  
   Use `/nobypass reload` to apply changes without restarting the server.



## 🔔 Webhook Notifications
The plugin supports Discord webhooks to notify administrators of blocked connections. Customize the notification format and embed color directly in the configuration file.



## 🖥️ Compatibility
- **Minecraft Versions**: Fully compatible with Velocity-based servers.



## ⚒️ How to Compile the Project

### Prerequisites
- **Java 17** or higher
- **Apache Maven** installed

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/OtakuSweett/NoBypass
   cd NoBypass
   ```
2. Compile the project:
   ```bash
   mvn clean package
   ```
3. Locate the compiled JAR file:  
   The compiled `NoBypass.jar` will be in the `target/` directory.

4. Place the JAR file in your Velocity server's `plugins` folder.



## 📝 License
This plugin is licensed under the MIT License.

<img width="96" height="96" alt="image" src="https://github.com/user-attachments/assets/b2c9ec9d-b1bb-4a2e-9f55-79e3fd8ecb85" />



## 🤝 Contributing
We welcome contributions to improve NoBypass! Please submit a pull request or open an issue to suggest changes or report bugs.



