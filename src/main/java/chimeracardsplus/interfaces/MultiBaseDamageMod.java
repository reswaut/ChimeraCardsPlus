package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface MultiBaseDamageMod {
    float modifyMultiBaseDamage(float baseDamage, AbstractMonster monster, AbstractCard card, AbstractMonster target);
}
