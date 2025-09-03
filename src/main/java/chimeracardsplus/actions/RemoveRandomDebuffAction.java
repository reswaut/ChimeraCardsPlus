package chimeracardsplus.actions;

import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RemoveRandomDebuffAction extends AbstractGameAction {
    public RemoveRandomDebuffAction(AbstractCreature target, AbstractCreature source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void update() {
        ArrayList<String> debuffs = target.powers.stream().filter(p -> p.type == PowerType.DEBUFF).map(p -> p.ID).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.DEFAULT_LIST_SIZE)));
        if (!debuffs.isEmpty()) {
            addToBot(new RemoveSpecificPowerAction(target, source, debuffs.get(AbstractDungeon.miscRng.random(0, debuffs.size() - 1))));
        }
        isDone = true;
    }
}
