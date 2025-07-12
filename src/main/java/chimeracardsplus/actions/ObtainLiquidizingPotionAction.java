package chimeracardsplus.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;

public class ObtainLiquidizingPotionAction extends ObtainPotionAction {
    public ObtainLiquidizingPotionAction() {
        super(AbstractDungeon.returnRandomPotion(true));
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "clearPostCombatActions"
    )
    public static class DontClearObtainPotionActionPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(Instanceof i) throws CannotCompileException {
                    try {
                        if (i.getType().getName().equals(HealAction.class.getName())) {
                            i.replace("{ $_ = $proceed($$) || ($1 instanceof " + ObtainLiquidizingPotionAction.class.getName() + "); }");
                        }
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}
