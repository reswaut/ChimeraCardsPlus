package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class IrregularMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(IrregularMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final double sigma = StrictMath.sqrt(2.0 * StrictMath.log(1.2));
    private boolean rolled;
    private float damageMultiplier, blockMultiplier, magicMultiplier;
    private int costIncrement;
    private float timer;

    public IrregularMod() {
        rolled = false;
        damageMultiplier = 0.0F;
        blockMultiplier = 0.0F;
        magicMultiplier = 0.0F;
        costIncrement = 0;
        timer = 0.0F;
    }

    public IrregularMod(float damageMultiplier, float blockMultiplier, float magicMultiplier, int costIncrement, float timer) {
        rolled = true;
        this.damageMultiplier = damageMultiplier;
        this.blockMultiplier = blockMultiplier;
        this.magicMultiplier = magicMultiplier;
        this.costIncrement = costIncrement;
        this.timer = timer;
    }

    private static float logNormal(com.megacrit.cardcrawl.random.Random rng) {
        return (float) StrictMath.exp(new java.util.Random(rng.randomLong()).nextGaussian() * sigma);
    }

    private void updateMultipliers(AbstractCard card, com.megacrit.cardcrawl.random.Random rng) {
        damageMultiplier = logNormal(rng);
        blockMultiplier = logNormal(rng);
        if (card.baseMagicNumber >= 1 && usesMagic(card)) {
            magicMultiplier = logNormal(rng);
        }
        if (cardCheck(card, c -> c.cost >= 0 && doesntUpgradeCost())) {
            card.cost -= costIncrement;
            costIncrement = 0;
            int choice = rng.random((1 << card.cost + 1) - 2);
            for (int i = 0; i < card.cost; ++i) {
                if (choice < (1 << i + 1) - 1) {
                    costIncrement = rng.randomBoolean() ? card.cost - i : i - card.cost;
                    break;
                }
            }
            card.cost += costIncrement;
            card.costForTurn = card.cost;
        }
    }

    @Override
    public void onUpdate(AbstractCard card) {
        if (CardCrawlGame.isInARun()) {
            return;
        }
        if (timer <= 0.0F || !rolled) {
            rolled = true;
            updateMultipliers(card, new com.megacrit.cardcrawl.random.Random(System.currentTimeMillis()));
            timer = 2.0F;
        }
        timer -= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!CardCrawlGame.isInARun() || rolled) {
            return;
        }
        rolled = true;
        updateMultipliers(card, AbstractDungeon.miscRng);
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.baseDamage >= 1 || abstractCard.baseBlock >= 1 || abstractCard.baseMagicNumber >= 1 && usesMagic(abstractCard);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F && damageMultiplier > 0.0F ? damage * damageMultiplier : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F && blockMultiplier > 0.0F ? block * blockMultiplier : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return magic > 0.0F && magicMultiplier > 0.0F ? magic * magicMultiplier : magic;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return rolled ? new IrregularMod(damageMultiplier, blockMultiplier, magicMultiplier, costIncrement, timer) : new IrregularMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}