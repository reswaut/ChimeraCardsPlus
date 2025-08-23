package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class LabelMod extends AbstractCardModifier {
    private final String id;

    public LabelMod(String id) {
        this.id = id;
    }

    public static String makeLabelModID(String name) {
        return ChimeraCardsPlus.makeID(LabelMod.class.getName() + ':' + name);
    }

    @Override
    public String identifier(AbstractCard card) {
        return id;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new LabelMod(id);
    }
}
