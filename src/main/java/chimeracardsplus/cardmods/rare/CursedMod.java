package chimeracardsplus.cardmods.rare;

import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class CursedMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(CursedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private String curseID;

    public CursedMod() {
        this(null);
    }
    public CursedMod(String curseID) {
        this.curseID = curseID;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (curseID == null && CardCrawlGame.isInARun()) {
            curseID = AbstractDungeon.returnRandomCurse().cardID;
        }
        if (curseID != null) {
            MultiCardPreview.add(card, CardLibrary.getCard(curseID));
        }
        card.cost -= 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 1 && doesntUpgradeCost() && c.rarity != CardRarity.BASIC && c.type != CardType.CURSE) && (!CardCrawlGame.isInARun() || AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() instanceof MonsterRoom);
    }

    @Override
    public boolean onObtain(AbstractCard card) {
        if (curseID == null) {
            curseID = AbstractDungeon.returnRandomCurse().cardID;
        }
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(CardLibrary.getCopy(curseID), (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2));
        return true;
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}