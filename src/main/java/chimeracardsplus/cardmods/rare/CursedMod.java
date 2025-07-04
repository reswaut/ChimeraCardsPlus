package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;
import static com.megacrit.cardcrawl.core.CardCrawlGame.isInARun;

public class CursedMod extends AbstractAugment implements TriggerOnObtainMod {
    public static final String ID = ChimeraCardsPlus.makeID(CursedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private String curseID;

    public CursedMod() {
    }

    public CursedMod(String curseID) {
        this.curseID = curseID;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (curseID == null && isInARun()) {
            curseID = AbstractDungeon.returnRandomCurse().cardID;
        }
        if (curseID != null) {
            MultiCardPreview.add(card, CardLibrary.getCard(curseID));
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 0) ? (damage * 2.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 0) ? (block * 2.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 0 && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 2.0F) : magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.rarity != AbstractCard.CardRarity.BASIC && c.type != AbstractCard.CardType.CURSE)
                && (card.baseBlock > 0 || card.baseDamage > 0 || (card.baseMagicNumber > 0 && doesntDowngradeMagicNoUseChecks(card)))
                && !(AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom);
    }

    @Override
    public void onObtain(AbstractCard card) {
        if (curseID == null) {
            curseID = AbstractDungeon.returnRandomCurse().cardID;
        }
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(CardLibrary.getCopy(curseID), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
    }

    @Override
    public String getPrefix() {
        if (curseID == null) {
            return TEXT[0];
        }
        return String.format(TEXT[1], CardLibrary.getCard(curseID).name);
    }

    @Override
    public String getSuffix() {
        return TEXT[2];
    }

    @Override
    public String getAugmentDescription() {
        if (curseID == null) {
            return TEXT[3];
        }
        return String.format(TEXT[4], FormatHelper.prefixWords(CardLibrary.getCard(curseID).name, "#r"));
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (curseID == null) {
            return insertAfterText(rawDescription, String.format(CARD_TEXT[0]));
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[1], FormatHelper.prefixWords(CardLibrary.getCard(curseID).name, "*")));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CursedMod(curseID);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}