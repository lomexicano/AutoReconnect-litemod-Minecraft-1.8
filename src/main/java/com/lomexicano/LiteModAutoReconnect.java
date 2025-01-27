package com.lomexicano;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.modconfig.*;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import java.io.File;

@ExposableOptions(strategy = ConfigStrategy.Unversioned, filename = "AutoReconnect.json")
public class LiteModAutoReconnect implements Tickable, Configurable {
    @Expose
    public long reconnectDelay = 10000;
    private static LiteModAutoReconnect instance;


    public LiteModAutoReconnect() {
        instance = this; // Atribui a instância ao singleton
    }
    public static LiteModAutoReconnect getInstance() {
        return instance;
    }
    private boolean shouldReconnect = false;

    private long reconnectStartTime = 0;

    private final Minecraft mc = Minecraft.getMinecraft();
    private ServerData lastServerData;


    public String getName() {
        return "AutoReconnect";
    }

    public String getVersion() {
        return "1.0";
    }

    public void init(File configPath) {
        LiteLoader.getInstance().registerExposable(this, null);
        writeConfig();
    }


    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        // Inicialização adicional, se necessário
    }

    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (mc.currentScreen instanceof GuiDisconnected) {
            // Inicializa a reconexão ao ser desconectado
            if (!shouldReconnect) {
                shouldReconnect = true;
                reconnectStartTime = System.currentTimeMillis();
                lastServerData = mc.getCurrentServerData(); // Armazena os dados do último servidor
            }

            // Calcula o tempo restante com duas casas decimais
            long elapsedTime = System.currentTimeMillis() - reconnectStartTime;
            double countdown = (reconnectDelay - elapsedTime) / 1000.0;

            // Exibe a contagem regressiva na tela
            if (countdown > 0) {
                GuiScreen currentScreen = mc.currentScreen;

                // Texto fixo (branco)
                String fixedText = "Tentando reconectar em ";
                String countdownText = String.format("%.2f segundos...", countdown);

                // Calcula a largura do texto fixo
                int fixedTextWidth = mc.fontRendererObj.getStringWidth(fixedText);


                currentScreen.drawCenteredString(
                        mc.fontRendererObj,
                        fixedText,
                        currentScreen.width / 2,
                        currentScreen.height / 2 + 50,
                        0xFFFFFF
                );

                // Calcula a largura do número (do countdown)
                int countdownWidth = mc.fontRendererObj.getStringWidth(String.format("%.2f", countdown));

                // Desenha o número do countdown em verde claro
                currentScreen.drawString(
                        mc.fontRendererObj,
                        String.format("%.2f", countdown),
                        currentScreen.width / 2 + fixedTextWidth / 2 + 2,  // Ajusta a posição com base na largura do texto fixo
                        currentScreen.height / 2 + 50,
                        0x00FF00  // Verde claro
                );
            } else {
                // Inicia a reconexão
                shouldReconnect = false;
                reconnectToServer();
            }
        } else if (mc.currentScreen instanceof GuiMainMenu) {
            // Cancela a reconexão se o jogador voltar ao menu principal
            shouldReconnect = false;
        }
    }

    private void reconnectToServer() {
        if (lastServerData != null) {
            // Reconecta ao último servidor
            mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, lastServerData));
        }
    }

    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
        // Atualização de configurações, se necessário
    }



    public void writeConfig() {
        LiteLoader.getInstance().writeConfig(this);
    }


    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return AutoReconnectConfigPanel.class;
    }


}


