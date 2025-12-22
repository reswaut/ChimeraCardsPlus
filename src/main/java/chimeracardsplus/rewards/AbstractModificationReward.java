package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomReward;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.patches.AbstractAugmentPlusPatches;
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
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class AbstractModificationReward extends CustomReward {
    protected static final Gson gson = new Gson();
    protected static final CardActionAnalyzer cardActionAnalyzer = new CardActionAnalyzer();
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

    protected static <T> T loadSaveFromReward(RewardSave rewardSave, Class<T> tClass) {
        T save;
        try {
            save = gson.fromJson(rewardSave.id, tClass);
        } catch (JsonSyntaxException e) {
            ChimeraCardsPlus.logger.error("Unable to load reward save {} to save type {}", rewardSave, tClass.getName());
            return null;
        }
        return save;
    }


    protected static JsonElement modifierToJsonTree(AbstractAugment modifier) {
        return modifierGson.toJsonTree(modifier, modifierType);
    }

    public abstract void replaceCard(AbstractCard oldCard, AbstractCard newCard);

    public void removeCard(AbstractCard oldCard) {
        if (sourceCards.contains(oldCard)) {
            sourceCards.clear();
            resultCards.clear();
        }
    }

    public boolean isInvalid() {
        return sourceCards.isEmpty() || relicLink != null && !AbstractDungeon.combatRewardScreen.rewards.contains(relicLink);
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
            AbstractCard card = resultCards.get(i);
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onObtainCard(card);
            }
            AbstractAugmentPlusPatches.onObtainCard(card);
            AbstractDungeon.player.masterDeck.addToTop(card);
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMasterDeckChange();
            }
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

    public interface AbstractRewardGenerator<T extends AbstractModificationReward> {
        long getGlobalWeight();

        T generate();

        T onLoad(RewardSave rewardSave);

        RewardSave onSave(AbstractModificationReward customReward, int amount);

        Class<T> getRewardClass();
    }
}
