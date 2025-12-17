package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomReward;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.screens.ModificationRewardScreen.CurrentScreenEnum;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class AbstractModificationReward extends CustomReward {
    protected static final Gson gson = new Gson();
    private static final String ID = ChimeraCardsPlus.makeID(AbstractModificationReward.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final Gson modifierGson;
    private static final Type modifierType = new TypeToken<AbstractCardModifier>() {
    }.getType();

    static {
        GsonBuilder builder = new GsonBuilder();
        if (CardModifierPatches.modifierAdapter == null) {
            CardModifierPatches.initializeAdapterFactory();
        }
        builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
        modifierGson = builder.create();
    }

    public final ArrayList<AbstractCard> sourceCards = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
    public final ArrayList<AbstractCard> resultCards = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);

    protected AbstractModificationReward(RewardType type) {
        super(ImageMaster.REWARD_CARD_NORMAL, TEXT[0], type);
    }

    protected static AbstractAugment modifierFromJson(JsonElement element) {
        AbstractCardModifier cardModifier;
        try {
            cardModifier = modifierGson.fromJson(element, modifierType);
        } catch (JsonSyntaxException e) {
            ChimeraCardsPlus.logger.error("Unable to load card mod: {}", element);
            return null;
        }
        return (AbstractAugment) cardModifier;
    }

    protected static JsonElement modifierToJsonTree(AbstractAugment modifier) {
        return modifierGson.toJsonTree(modifier, modifierType);
    }

    public abstract void replaceCard(AbstractCard oldCard, AbstractCard newCard);

    public void removeCard(AbstractCard oldCard) {
        if (sourceCards.contains(oldCard)) {
            sourceCards.clear();
        }
    }

    public boolean isInvalid() {
        return sourceCards.isEmpty();
    }

    public void takeReward() {
        AbstractDungeon.combatRewardScreen.rewards.remove(this);
        if (relicLink != null) {
            AbstractDungeon.combatRewardScreen.rewards.remove(relicLink);
        }

        int sourceSize = sourceCards.size(), resultSize = resultCards.size();
        int replaceSize = Math.min(sourceSize, resultSize);
        for (int i = 0; i < replaceSize; ++i) {
            AbstractCard sourceCard = sourceCards.get(i);
            AbstractCard resultCard = resultCards.get(i);
            int index = AbstractDungeon.player.masterDeck.group.indexOf(sourceCard);
            if (index == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", sourceCard);
                AbstractDungeon.player.masterDeck.group.add(resultCard);
            } else {
                AbstractDungeon.player.masterDeck.group.set(index, resultCard);
                for (RewardItem item : AbstractDungeon.combatRewardScreen.rewards) {
                    if (item instanceof AbstractModificationReward) {
                        ((AbstractModificationReward) item).replaceCard(sourceCard, resultCard);
                    }
                }
            }
        }
        for (int i = replaceSize; i < resultSize; ++i) {
            AbstractDungeon.player.masterDeck.group.add(resultCards.get(i));
        }
        for (int i = replaceSize; i < sourceSize; ++i) {
            AbstractCard sourceCard = sourceCards.get(i);
            AbstractDungeon.player.masterDeck.removeCard(sourceCard);
            for (RewardItem item : AbstractDungeon.combatRewardScreen.rewards) {
                if (item instanceof AbstractModificationReward) {
                    ((AbstractModificationReward) item).removeCard(sourceCard);
                }
            }
        }

        AbstractDungeon.combatRewardScreen.rewards.removeIf(item -> item instanceof AbstractModificationReward && ((AbstractModificationReward) item).isInvalid());
        AbstractDungeon.combatRewardScreen.positionRewards();
        if (AbstractDungeon.combatRewardScreen.rewards.isEmpty()) {
            AbstractDungeon.combatRewardScreen.hasTakenAll = true;
            AbstractDungeon.overlayMenu.proceedButton.show();
        }

        float padX = AbstractCard.IMG_WIDTH + 40.0F * Settings.scale;
        float x = (Settings.WIDTH - (resultCards.size() - 1) * padX) / 2.0F;
        for (AbstractCard card : resultCards) {
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), x, Settings.HEIGHT / 2.0F));
            x += padX;
        }
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }

    @Override
    public boolean claimReward() {
        if (AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD) {
            BaseMod.openCustomScreen(CurrentScreenEnum.CHIMERA_MODIFICATION_REWARD, this);
        }
        return false;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (relicLink != null) {
            ReflectionHacks.privateMethod(RewardItem.class, "renderRelicLink", SpriteBatch.class).invoke(this, sb);
        }
    }
}
