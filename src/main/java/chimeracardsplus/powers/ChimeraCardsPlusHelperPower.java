package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnDrawPileShufflePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ChimeraCardsPlusHelperPower extends AbstractPower implements InvisiblePower, OnDrawPileShufflePower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(ChimeraCardsPlusHelperPower.class.getSimpleName());
    public static final String NAME = "Chimera Cards+ Helper";
    private static final String[] DESCRIPTIONS = {"Chimera Cards+ helper power. You shouldn't be seeing this normally."};

    public ChimeraCardsPlusHelperPower(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        amount = -1;
        updateDescription();
        loadRegion("unawakened");
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onShuffle() {
        ChimeraCardsPlus.battleActionInfoManager.onShuffle();
    }
}
