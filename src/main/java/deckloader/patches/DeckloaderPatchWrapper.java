package deckloader.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.ConvertHelper;
import basemod.patches.whatmod.WhatMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import deckloader.DeckloaderMod;
import deckloader.helpers.ModTester;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeckloaderPatchWrapper {

    @SpirePatch(
            clz=basemod.DevConsole.class,
            method="execute"
    )
    public static class Deckloader {

        // Since we add commands, we should also add it to the autocompletion of basemod.AutoComplete.
        // But since I currently don't know how I could do that in a way that would not mean "Replace the whole
        // COMMANDS String[] with my own", I will currently avoid doing this.
        @SpireInsertPatch(
                loc=109,
                localvars={"tokens"}
        )
        public static void Insert(String[] tokens) {

            switch (tokens[0].toLowerCase()) {
                case "deckload": {
                    try {
                        cmdDeckload(tokens);
                    }
                    catch (Exception e) {
                        DeckloaderMod.logger.catching(e);
                        cmdDeckloadError();
                    }
                    break;
                }
                case "deckbackup": {
                    try {
                        cmdDeckbackup(tokens);
                    }
                    catch (Exception e) {
                        DeckloaderMod.logger.catching(e);
                        cmdDeckbackupError();
                    }
                    break;
                }
                case "dumpcontent": {
                    try {
                        cmdDumpcontent(tokens);
                    }
                    catch (Exception e) {
                        DeckloaderMod.logger.catching(e);
                        cmdDumpcontentError();
                    }
                }
                default: {
                    DeckloaderMod.logger.info("The command isn't one of DeckloaderMod.");
                    break;
                }
            }
        }

        public static void cmdDeckload(String[] tokens)
                throws  IOException {
            if (tokens.length < 2) {
                cmdDeckloadHelp();
                return;
            }

            // Deleting our filthy old build
            for (String relicName : AbstractDungeon.player.getRelicNames()) { AbstractDungeon.player.loseRelic(relicName); }
            for (String str : AbstractDungeon.player.masterDeck.getCardNames()) { AbstractDungeon.player.masterDeck.removeCard(str); }

            String state = null;
            String line = null;

            String filename = tokens[1];

            // Reading through the file to get all our new build
            BufferedReader reader = new BufferedReader(new FileReader("./builds/" + filename + ".txt"));
            while ((line = reader.readLine()) != null) {

                //States of the build
                if (line.contains("[Cards]")) { state = "cards"; continue; }
                else if (line.contains("[Relics]")) { state = "relics"; continue; }

                if (state == null) continue;
                else if (state.equals("cards")) {
                    String[] elements = line.split(" • ");
                    AbstractCard c = CardLibrary.getCard(elements[0]).makeCopy();
                    int upgradeCount = 0;
                    upgradeCount = ConvertHelper.tryParseInt(elements[1], 0);
                    for (int i = 0; i < upgradeCount; i++) { c.upgrade(); }
                    UnlockTracker.markCardAsSeen(c.cardID);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                    DeckloaderMod.logger.info("Deckloading: Added card \"" + c.cardID + "\", upgraded " + upgradeCount + "times.");
                }
                else if (state.equals("relics")) {
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f,
                            RelicLibrary.getRelic(line).makeCopy());
                    DeckloaderMod.logger.info("Deckloading: Added relic \"" + line + "\".");
                }
            }

            basemod.DevConsole.log("Your build was successfully loaded !");

        }

        public static void cmdDeckbackup(String[] tokens)
                throws IOException {
            if (tokens.length < 2) {
                cmdDeckbackupHelp();
                return;
            }

            String filename = tokens[1];

            BufferedWriter writer = new BufferedWriter(new FileWriter("./builds/" + filename + ".txt"));

            writer.write("[Cards]");
            writer.write("\r\n");
            for (CardSave card : AbstractDungeon.player.masterDeck.getCardDeck()) {
                writer.write(card.id + " • " + card.upgrades);
                writer.write("\r\n");
                DeckloaderMod.logger.info("Decksaving: Saved card \"" + card.id + "\", upgraded " + card.upgrades + "times.");
            }

            writer.write("[Relics]");
            writer.write("\r\n");
            for (String str : AbstractDungeon.player.getRelicNames()) {
                writer.write(str);
                writer.write("\r\n");
                DeckloaderMod.logger.info("Decksaving: Saved relic \"" + str + "\".");
            }
            writer.close();

            basemod.DevConsole.log("Your build was saved in " + filename + ".txt !");

            return;
        }

        public static void cmdDumpcontent(String[] tokens)
                throws IOException {
            if (tokens.length < 2) {
                cmdDumpcontentHelp();
                return;
            }

            String filename = tokens[1];

            BufferedWriter writer = new BufferedWriter(new FileWriter("./builds/" + filename + ".txt"));

            writer.write("[Cards]");
            writer.write("\r\n");
            writer.write("[Modded?/ModName] | [CardColor] | [Type] | [Rarity] | [Cost] | [CardID] | [Name] | [Raw Description]");
            writer.write("\r\n");
            for (AbstractCard card : CardLibrary.getAllCards()) {
                String modname = null;
                if (ModTester.isModded(card.getClass()) == true) { modname = ModTester.getModName(card.getClass()); }
                else { modname = "Vanilla"; }
                writer.write(modname + " | " + card.color.name() + " | " + card.type.name() + " | " + card.rarity.name() + " | " + card.cost + " | " + card.cardID + " | " + card.name + " | " + card.rawDescription);
                writer.write("\r\n");
                DeckloaderMod.logger.info("Deckdumping: Dumped card \"" + card.cardID + ".");
            }

            writer.write("[Relics]");
            writer.write("\r\n");
            writer.write("[Modded?/Modname] | [Tier] | [RelicID] | [Name] | [Description]");
            writer.write("\r\n");

            HashMap<String, AbstractRelic> sharedRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
                    .getPrivateStatic(RelicLibrary.class, "sharedRelics");
            WriteRelic(writer, sharedRelics);
            HashMap<String, AbstractRelic> redRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
                    .getPrivateStatic(RelicLibrary.class, "redRelics");
            WriteRelic(writer, redRelics);
            HashMap<String, AbstractRelic> greenRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
                    .getPrivateStatic(RelicLibrary.class, "greenRelics");
            WriteRelic(writer, greenRelics);
            HashMap<String, AbstractRelic> blueRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
                    .getPrivateStatic(RelicLibrary.class, "blueRelics");
            WriteRelic(writer, blueRelics);

            DeckloaderMod.logger.info("Deckdumping: Dumped all relics.");

/*
            writer.write("[Modded Relics]");
            writer.write("\r\n");

            //Modded relics. BaseMod got something for me here. Hurray!
            HashMap<AbstractCard.CardColor, HashMap<String, AbstractRelic>> customRelics = BaseMod.getAllCustomRelics();
            for (HashMap.Entry<AbstractCard.CardColor, HashMap<String, AbstractRelic>> en : customRelics.entrySet()) {
                String color = en.getKey().name();
                HashMap<String, AbstractRelic> relics = en.getValue();
                for (HashMap.Entry<String, AbstractRelic> _en : relics.entrySet()) {
                    AbstractRelic r = _en.getValue();
                    writer.write(color + " | " + r.tier + " | " + r.relicId + " | " + r.name + " | " + r.description);
                    writer.write("\r\n");
                    DeckloaderMod.logger.info("Deckdumping: Dumped custom relic \"" + r.name + "\".");
                }
            }
*/

            writer.close();

            DevConsole.log("Cards and relics successfully dumped in " + filename + ".txt !");

            return;
        }

        private static void WriteRelic(BufferedWriter writer, HashMap<String, AbstractRelic> blueRelics) throws IOException {
            if (blueRelics != null) {
                for (HashMap.Entry<String, AbstractRelic> _en : blueRelics.entrySet()) {
                    AbstractRelic r = _en.getValue();
                    String modname = null;
                    if (ModTester.isModded(r.getClass()) == true) { modname = ModTester.getModName(r.getClass()); }
                    else { modname = "Vanilla"; }
                    writer.write( modname + " | " + r.tier + " | " + r.relicId + " | " + r.name + " | " + r.description);
                    writer.write("\r\n");
                }
            }
        }

        public static void cmdDeckloadHelp() {
            couldNotParse();
            basemod.DevConsole.log("You need to enter the name of the file you want to load.");
            basemod.DevConsole.log("It must be located at GameFolder/builds.");
            basemod.DevConsole.log("You can omit the file's extension.");
        }

        public static void cmdDeckbackupHelp() {
            couldNotParse();
            basemod.DevConsole.log("You need to enter the name of the file to backup your build to.");
            basemod.DevConsole.log("It will be located at GameFolder/builds.");
            basemod.DevConsole.log("The extension will be added automatically.");
        }

        public static void cmdDeckloadError() {
            basemod.DevConsole.log("Something wrong happened while trying to load from the file.");
        }

        public static void cmdDeckbackupError() {
            basemod.DevConsole.log("Something wrong happened while trying to write to the file.");
        }

        public static void cmdDumpcontentHelp() {
            couldNotParse();
            basemod.DevConsole.log("You need to enter the name of the file to dump all cards and relics ids to.");
            basemod.DevConsole.log("It will be located at GameFolder/builds.");
            basemod.DevConsole.log("The extension will be added automatically.");
        }

        public static void cmdDumpcontentError() {
            basemod.DevConsole.log("Something wrong happened while dumping cards and relics.");
        }

        public static void couldNotParse() {
            basemod.DevConsole.log("Could not parse previous command.");
        }
    }
}