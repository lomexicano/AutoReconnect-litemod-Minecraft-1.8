package com.lomexicano;

import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class AutoReconnectConfigPanel implements ConfigPanel {

    LiteModAutoReconnect autoReconnect = LiteModAutoReconnect.getInstance();

    //////////////////
    // CONFIG PANEL //
    //////////////////
    private GuiTextField reconnectDelayField;

    public String getPanelTitle() {
        return "AutoReconnect Settings";
    }

    public int getContentHeight() {
        return -1;
    }
    String label;
    int labelWidth;
    int labelXPosition;
    int labelYPosition;

    @Override
    public void onPanelShown(ConfigPanelHost host) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        // Definindo a largura da caixa de texto
        int textFieldWidth = 90;

        // Definindo a posição da caixa de texto
        int xPosition = host.getWidth() / 2 - textFieldWidth / 2;
        int yPosition = host.getHeight() / 2 - 10;

        // Criando a GuiTextField
        this.reconnectDelayField = new GuiTextField(0, fontRenderer, xPosition, yPosition, textFieldWidth, 20);

        String texto = String.valueOf(autoReconnect.reconnectDelay);

        // Definindo o valor da caixa de texto com o valor atual de reconnectDelay
        this.reconnectDelayField.setText(texto);  // Aqui, sincroniza o valor

        // Desenhando o texto à esquerda da caixa de texto
        label = "Tempo (ms): ";
        labelWidth = fontRenderer.getStringWidth(label);
        labelXPosition = xPosition - labelWidth - 5; // 5px de margem entre o texto e a caixa
        labelYPosition = yPosition + 5; // Ajustando a altura do texto
    }

    public void onPanelResize(ConfigPanelHost host) {}

    @Override
    public void onPanelHidden() {
        try {
            // Tenta converter o valor do campo de texto
            long newDelay = Long.parseLong(this.reconnectDelayField.getText());

            // Se o valor for negativo, define um valor padrão
            if (newDelay < 0) {
                autoReconnect.reconnectDelay = 10000; // Valor padrão
            } else {
                autoReconnect.reconnectDelay = newDelay;
            }

        } catch (NumberFormatException e) {
            // Se o valor não for um número válido, mantém o valor anterior
            autoReconnect.reconnectDelay = 10000; // Fixa um valor padrão em caso de erro
        }

        // Agora, depois de atualizar o valor de reconnectDelay, chame writeConfig()
        autoReconnect.writeConfig();  // Grava a configuração no arquivo
    }

    @Override
    public void onTick(ConfigPanelHost configPanelHost){ }

    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        this.reconnectDelayField.drawTextBox();
        Minecraft.getMinecraft().currentScreen.drawString(Minecraft.getMinecraft().fontRendererObj, label, labelXPosition, labelYPosition, 0xFFFFFF);
    }

    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        this.reconnectDelayField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {}

    public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {}

    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
        this.reconnectDelayField.textboxKeyTyped(keyChar, keyCode);
    }
}
