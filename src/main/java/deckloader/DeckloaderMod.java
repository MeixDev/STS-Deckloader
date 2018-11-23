package deckloader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostCreateShopPotionSubscriber;
import basemod.interfaces.PostInitializeSubscriber;

import deckloader.patches.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class DeckloaderMod implements
    PostInitializeSubscriber {
    public static final Logger logger = LogManager.getLogger(DeckloaderMod.class.getName());

    //This is for the in-game mod settings panel.
    public static final String MODNAME = "Deckloader";
    public static final String AUTHOR = "MeixDev";
    public static final String DESCRIPTION = "Allows you to backup and load decks and relics sets through BaseMod's console.";

    // Image folder name
    private static final String DEFAULT_MOD_ASSETS_FOLDER = "deckloaderModResources/images";

    //Mod Badge
    public static final String BADGE_IMAGE = "Badge.png";

    /**
     * Makes a full path for a resource path
     *
     * @param resource the resource, must *NOT* have a leading "/"
     * @return the full path
     */
    public static final String makePath(String resource) {
        return DEFAULT_MOD_ASSETS_FOLDER + "/" + resource;
    }

    // =============== SUBSCRIBE, CREATE THE COLOR, INITIALIZE =================

    public DeckloaderMod() {
        logger.info("Suscribing to BaseMod Hooks");
        BaseMod.subscribe(this);
    }

    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("===== Initializing DeckloaderMod. =====");
        DeckloaderMod deckloaderMod = new DeckloaderMod();
        logger.info("===== /DeckloaderMod Initialized/ =====");
    }

    // =============== POST-INITIALIZE =================

    @Override
    public void receivePostInitialize() {

        logger.info("Loading Badge Image and mod options");
        // Load the Mod Badge
        Texture badgeTexture = new Texture(makePath(BADGE_IMAGE));

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        settingsPanel.addUIElement(new ModLabel("DeckloaderMod doesn't have any settings!", 400.0f, 700.0f,
                settingsPanel, (me) -> {}));
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        logger.info("Done loading badge Image and mod options");
    }

    // =============== / POST-INITIALIZE/ =================

    // this adds "ModName: " before the ID of any card/relic/power etc.
    // in order to avoid conflics if any other mod uses the same ID.
    public static String makeID(String idText) {
        return "DeckloaderMod: " + idText;
    }
}
