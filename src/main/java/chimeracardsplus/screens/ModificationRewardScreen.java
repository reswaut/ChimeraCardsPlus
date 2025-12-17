package chimeracardsplus.screens;

import basemod.abstracts.CustomScreen;
import chimeracardsplus.rewards.AbstractModificationReward;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

public class ModificationRewardScreen extends CustomScreen {
    private final GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(GridCardSelectScreen.TEXT[0]);
    private float arrowScale1 = 1.0F, arrowScale2 = 1.0F, arrowScale3 = 1.0F;
    private float arrowTimer = 0.0F;
    private AbstractModificationReward reward = null;

    public void open(AbstractModificationReward reward) {
        this.reward = reward;
        reopen();
    }

    @Override
    public CurrentScreen curScreen() {
        return CurrentScreenEnum.CHIMERA_MODIFICATION_REWARD;
    }

    @Override
    public void reopen() {
        AbstractDungeon.previousScreen = CurrentScreen.COMBAT_REWARD;

        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.dynamicBanner.appear(AbstractModificationReward.TEXT[1]);
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.showBlackScreen();

        AbstractDungeon.overlayMenu.cancelButton.show(CardRewardScreen.TEXT[0]);
        confirmButton.show();
        confirmButton.isDisabled = false;
    }

    @Override
    public void close() {
        AbstractDungeon.dynamicBanner.hide();

        AbstractDungeon.overlayMenu.cancelButton.hide();
        confirmButton.hide();
        confirmButton.isDisabled = true;
    }

    @Override
    public void update() {
        for (AbstractCard card : reward.sourceCards) {
            card.update();
        }
        for (AbstractCard card : reward.resultCards) {
            card.update();
        }
        confirmButton.update();
        if (confirmButton.hb.clicked) {
            confirmButton.hb.clicked = false;
            reward.takeReward();
            AbstractDungeon.closeCurrentScreen();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        renderArrows(sb);

        float padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;

        float sourceX = Settings.WIDTH * 0.36F - (reward.sourceCards.size() - 1) * padX;
        for (AbstractCard card : reward.sourceCards) {
            card.current_x = sourceX;
            card.current_y = Settings.HEIGHT / 2.0F;
            card.target_x = sourceX;
            card.target_y = Settings.HEIGHT / 2.0F;
            sourceX += padX;

            card.render(sb);
            card.updateHoverLogic();
            card.renderCardTip(sb);
        }

        float resultX = Settings.WIDTH * 0.63F;
        for (AbstractCard card : reward.resultCards) {
            card.current_x = resultX;
            card.current_y = Settings.HEIGHT / 2.0F;
            card.target_x = resultX;
            card.target_y = Settings.HEIGHT / 2.0F;
            resultX += padX;

            card.render(sb);
            card.updateHoverLogic();
            card.renderCardTip(sb);
        }

        confirmButton.render(sb);
    }

    @Override
    public boolean allowOpenDeck() {
        return true;
    }

    @Override
    public boolean allowOpenMap() {
        return true;
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
        AbstractDungeon.dynamicBanner.hide();
    }

    @Override
    public void openingDeck() {
        AbstractDungeon.previousScreen = curScreen();
        AbstractDungeon.dynamicBanner.hide();
    }

    @Override
    public void openingMap() {
        AbstractDungeon.previousScreen = curScreen();
        AbstractDungeon.dynamicBanner.hide();
    }

    private void renderArrows(SpriteBatch sb) {
        sb.setColor(Color.WHITE);

        float x = Settings.WIDTH / 2.0F - 73.0F * Settings.scale - 32.0F;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale1 * Settings.scale, arrowScale1 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale2 * Settings.scale, arrowScale2 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, arrowScale3 * Settings.scale, arrowScale3 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
        arrowScale1 = 0.8F + (MathUtils.cos(arrowTimer) + 1.0F) / 8.0F;
        arrowScale2 = 0.8F + (MathUtils.cos(arrowTimer - 0.8F) + 1.0F) / 8.0F;
        arrowScale3 = 0.8F + (MathUtils.cos(arrowTimer - 1.6F) + 1.0F) / 8.0F;
    }

    public static class CurrentScreenEnum {
        @SpireEnum
        public static CurrentScreen CHIMERA_MODIFICATION_REWARD;
    }
}
