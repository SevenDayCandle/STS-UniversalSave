package universalsettings;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.SaveSlotScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class UniversalSettingsPatches {
    private static boolean forDelete;
    private static int fakeSlot;

    @SpirePatch(
            clz = SaveHelper.class,
            method = "deletePrefs"
    )
    public static class SaveHelper_DeletePrefs {
        @SpirePrefixPatch
        public static void prefix(int slot) {
            forDelete = true;
        }

        @SpirePostfixPatch
        public static void postfix(int slot) {
            forDelete = false;
        }
    }

    @SpirePatch(
            clz = SaveHelper.class,
            method = "getPrefs"
    )
    public static class SaveHelper_GetPrefs {
        @SpirePrefixPatch
        public static void prefix(String name) {
            fakeSlot = CardCrawlGame.saveSlot;
            switch (name) {
                case "STSSound":
                case "STSGameplaySettings":
                case "STSInputSettings":
                case "STSInputSettings_Controller":
                    CardCrawlGame.saveSlot = 0;
            }
        }

        @SpirePostfixPatch
        public static void postfix(String name) {
            CardCrawlGame.saveSlot = fakeSlot;
        }
    }

    @SpirePatch(
            clz = SaveHelper.class,
            method = "slotName"
    )
    public static class SaveHelper_SlotName {
        @SpirePrefixPatch
        public static SpireReturn<String> prefix(String name, int slot) {
            if (forDelete) {
                switch (name) {
                    case "STSSound":
                    case "STSGameplaySettings":
                    case "STSInputSettings":
                    case "STSInputSettings_Controller":
                        return SpireReturn.Return(name + name); // Doesn't matter as long as deletion is foiled
                }
            }
            return SpireReturn.Continue();
        }
    }

}
