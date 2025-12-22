package chimeracardsplus.rewards;

import CardAugments.cardmods.common.*;
import CardAugments.cardmods.curse.ReturningMod;
import CardAugments.cardmods.curse.VoidingMod;
import CardAugments.cardmods.event.*;
import CardAugments.cardmods.rare.*;
import CardAugments.cardmods.uncommon.*;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.cardmods.common.*;
import chimeracardsplus.cardmods.rare.*;
import chimeracardsplus.cardmods.uncommon.*;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.mod.stslib.actions.defect.EvokeSpecificOrbAction;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.actions.utility.DrawPileToHandAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.JudgementAction;
import com.megacrit.cardcrawl.actions.watcher.LessonLearnedAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.blue.ForceField;
import com.megacrit.cardcrawl.cards.green.GlassKnife;
import com.megacrit.cardcrawl.cards.green.GrandFinale;
import com.megacrit.cardcrawl.cards.green.MasterfulStab;
import com.megacrit.cardcrawl.cards.purple.BowlingBash;
import com.megacrit.cardcrawl.cards.purple.FollowUp;
import com.megacrit.cardcrawl.cards.purple.SignatureMove;
import com.megacrit.cardcrawl.cards.red.BloodForBlood;
import com.megacrit.cardcrawl.cards.red.Clash;
import com.megacrit.cardcrawl.cards.red.HeavyBlade;
import com.megacrit.cardcrawl.cards.red.SearingBlow;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.cards.tempCards.Safety;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.cards.tempCards.Smite;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.*;

public class CardActionAnalyzer extends ExprEditor {
    private static final Map<String, Collection<String>> methodsToSearch;
    private static final Set<String> excludedPackages;
    private static final Set<Class<?>> spireTypePatchAnnotations;
    private static final Set<Class<?>> spireMethodPatchAnnotations;

    static {
        methodsToSearch = new HashMap<>(3);
        methodsToSearch.put(AbstractCard.class.getName(), Arrays.asList(
                "use",
                "triggerWhenDrawn",
                "triggerOnEndOfPlayerTurn",
                "triggerOnEndOfTurnForPlayingCard",
                "triggerOnOtherCardPlayed",
                "triggerOnManualDiscard",
                "triggerOnCardPlayed",
                "triggerOnScry",
                "triggerExhaustedCardsOnStanceChange",
                "atTurnStart",
                "atTurnStartPreDraw",
                "onChoseThisOption",
                "onRetained",
                "triggerOnExhaust"));
        methodsToSearch.put(AbstractGameAction.class.getName(), Collections.singletonList(
                "update"
        ));
        methodsToSearch.put(AbstractGameEffect.class.getName(), Collections.singletonList(
                "update"
        ));
        methodsToSearch.put(AbstractPower.class.getName(), Arrays.asList(
                "atStartOfTurn",
                "duringTurn",
                "atStartOfTurnPostDraw",
                "atEndOfTurn",
                "atEndOfTurnPreEndTurnCards",
                "atEndOfRound",
                "onScry",
                "onAttack",
                "onInflictDamage",
                "onCardDraw",
                "onUseCard",
                "onAfterUseCard",
                "wasHPLost",
                "onSpecificTrigger",
                "triggerMarks",
                "onDeath",
                "onExhaust",
                "onChangeStance",
                "onGainedBlock",
                "onRemove",
                "onEnergyRecharge",
                "onDrawOrDiscard",
                "onAfterCardPlayed",
                "onInitialApplication",
                "onApplyPower",
                "onVictory"
        ));

        excludedPackages = new HashSet<>(5);
        excludedPackages.add("com.badlogic.");
        excludedPackages.add("com.google.");
        excludedPackages.add("java.");
        excludedPackages.add("org.apache.");
        excludedPackages.add("sun.");

        spireTypePatchAnnotations = new HashSet<>(4);
        spireTypePatchAnnotations.add(SpirePatch.class);
        spireTypePatchAnnotations.add(SpirePatches.class);
        spireTypePatchAnnotations.add(SpirePatch2.class);
        spireTypePatchAnnotations.add(SpirePatches2.class);

        spireMethodPatchAnnotations = new HashSet<>(5);
        spireMethodPatchAnnotations.add(SpirePrefixPatch.class);
        spireMethodPatchAnnotations.add(SpirePostfixPatch.class);
        spireMethodPatchAnnotations.add(SpireInsertPatch.class);
        spireMethodPatchAnnotations.add(SpireInstrumentPatch.class);
        spireMethodPatchAnnotations.add(SpireRawPatch.class);
    }

    private final Map<String, List<String>> cardToModifier = new HashMap<>(Constants.EXPECTED_CARDS);
    private final Collection<String> visitedMethods = new HashSet<>(Constants.EXPECTED_METHODS);
    private final Collection<String> visitedClasses = new HashSet<>(Constants.EXPECTED_METHODS);

    public List<String> getAssociatedModifiers(String cardID) {
        analyzeCard(cardID);
        return cardToModifier.get(cardID);
    }

    private boolean usesActions(Class<?>... clzs) {
        return Arrays.stream(clzs).anyMatch(clz -> visitedClasses.contains(clz.getName()));
    }

    private boolean callsMethod(Class<?> clz, String methodName) {
        try {
            return visitedMethods.contains(Loader.getClassPool().get(clz.getName()).getDeclaredMethod(methodName).getLongName());
        } catch (NotFoundException ignored) {
        }
        return false;
    }

    private void visitClass(Class<?> clz) {
        try {
            CtClass ctClass = Loader.getClassPool().get(clz.getName());
            ctClass.defrost();
            searchInClass(ctClass);
        } catch (NotFoundException ignored) {
        }
    }

    private void searchInClass(CtClass ctClass) {
        if (ctClass == null) {
            return;
        }
        String className = ctClass.getName();
        if (visitedClasses.contains(className) || excludedPackages.stream().anyMatch(className::startsWith)) {
            return;
        }
        visitedClasses.add(className);
        if (spireTypePatchAnnotations.stream().anyMatch(ctClass::hasAnnotation)) {
            return;
        }

        String baseClassToSearch = null;
        try {
            for (CtClass superClass = ctClass; superClass != null; superClass = superClass.getSuperclass()) {
                String superClassName = superClass.getName();
                if (methodsToSearch.containsKey(superClassName)) {
                    baseClassToSearch = superClassName;
                    break;
                }
            }
        } catch (NotFoundException ignored) {
        }
        if (baseClassToSearch == null) {
            return;
        }

        for (String methodName : methodsToSearch.get(baseClassToSearch)) {
            try {
                for (CtClass superClass = ctClass; superClass != null && !baseClassToSearch.equals(superClass.getName()); superClass = superClass.getSuperclass()) {
                    try {
                        searchInMethod(superClass.getDeclaredMethod(methodName));
                        break;
                    } catch (NotFoundException ignored) {
                    }
                }
            } catch (NotFoundException ignored) {
            }
        }
    }

    private void searchInMethod(CtMethod ctMethod) {
        if (ctMethod == null) {
            return;
        }
        String methodName = ctMethod.getLongName();
        if (visitedMethods.contains(methodName) || excludedPackages.stream().anyMatch(methodName::startsWith)) {
            return;
        }
        visitedMethods.add(methodName);
        if (spireMethodPatchAnnotations.stream().anyMatch(ctMethod::hasAnnotation)) {
            return;
        }

        try {
            ctMethod.instrument(this);
        } catch (CannotCompileException ignored) {
        }
    }

    @Override
    public void edit(MethodCall m) {
        try {
            searchInMethod(m.getMethod());
        } catch (NotFoundException ignored) {
        }
    }

    @Override
    public void edit(NewExpr e) {
        try {
            searchInClass(e.getConstructor().getDeclaringClass());
        } catch (NotFoundException | RuntimeException ignored) {
        }
    }

    private void analyzeCard(String cardID) {
        if (cardToModifier.containsKey(cardID) && !Settings.isDebug) {
            return;
        }
        AbstractCard card = CardLibrary.getCard(cardID);
        if (card == null) {
            return;
        }

        visitedClasses.clear();
        visitedMethods.clear();
        visitClass(card.getClass());
        if (card.cardsToPreview != null) {
            visitClass(card.cardsToPreview.getClass());
        }
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (c != null) {
                visitClass(c.getClass());
            }
        }

        Set<String> modifiers = new HashSet<>(Constants.EXPECTED_MODIFIERS);
        if (card.cost == -1) {
            modifiers.add(XMod.ID);
        }
        if (card.cost == 0) {
            modifiers.add(TinyMod.ID);
        }
        if (card.cost == 3) {
            modifiers.add(BludgeonMod.ID);
            modifiers.add(FormMod.ID);
        }
        if (card.cost >= 3) {
            modifiers.add(AmplifiedMod.ID);
            modifiers.add(MassiveMod.ID);
        }
        if (card.cost >= 5) {
            modifiers.add(MeteorMod.ID);
        }

        if (card.exhaust) {
            modifiers.add(FanaticMod.ID);
            modifiers.add(FragileMod.ID);
        }
        if (card.isEthereal) {
            modifiers.add(BrutalMod.ID);
            modifiers.add(GhostlyMod.ID);
            modifiers.add(MementoMod.ID);
            modifiers.add(PagedMod.ID);
            modifiers.add(SculptingMod.ID);
            modifiers.add(ShiningMod.ID);
            modifiers.add(VoidingMod.ID);
        }
        if (card.isInnate) {
            modifiers.add(BootMod.ID);
            modifiers.add(DramaticMod.ID);
            modifiers.add(ProudMod.ID);
        }
        if (card.selfRetain) {
            modifiers.add(AuraMod.ID);
            modifiers.add(EstablishedMod.ID);
            modifiers.add(PatientMod.ID);
            modifiers.add(PerseverantMod.ID);
            modifiers.add(StickyMod.ID);
        }
        if (card.shuffleBackIntoDrawPile || usesActions(ReboundPower.class, ForethoughtAction.class, PutOnDeckAction.class, SetupAction.class)) {
            modifiers.add(ForethoughtMod.ID);
            modifiers.add(MemorableMod.ID);
            modifiers.add(ProactiveMod.ID);
            modifiers.add(RecurringMod.ID);
            modifiers.add(ReshuffleMod.ID);
            modifiers.add(ReturningMod.ID);
            modifiers.add(SetupMod.ID);
            modifiers.add(SpringyMod.ID);
        }

        if (card.hasTag(CardTags.STRIKE)) {
            modifiers.add(PerfectMod.ID);
            modifiers.add(StrikeMod.ID);
        }

        boolean dealDamage = callsMethod(AbstractMonster.class, "damage");
        boolean gainBlock = usesActions(GainBlockAction.class);

        if (gainBlock) {
            modifiers.add(CounterMod.ID);
            modifiers.add(OrnamentalMod.ID);
            modifiers.add(PainlessMod.ID);
            modifiers.add(SharedMod.ID);
            modifiers.add(ShieldedMod.ID);
            modifiers.add(ToughMod.ID);
        }
        if (dealDamage && gainBlock) {
            modifiers.add(InvertedMod.ID);
            modifiers.add(WallopMod.ID);
            modifiers.add(WaveMod.ID);
        }

        if (callsMethod(CardGroup.class, "moveToExhaustPile")) {
            modifiers.add(BeyondMod.ID);
            modifiers.add(CinderyMod.ID);
            modifiers.add(ClearMod.ID);
            modifiers.add(EndMod.ID);
            modifiers.add(FiendMod.ID);
            modifiers.add(GritMod.ID);
            modifiers.add(PureMod.ID);
            modifiers.add(RecyclableMod.ID);
            modifiers.add(SeparateMod.ID);
        }
        if (callsMethod(AbstractMonster.class, "getIntentBaseDmg")) {
            modifiers.add(GoForTheMod.ID);
            modifiers.add(RunicMod.ID);
            modifiers.add(ObservantMod.ID);
            modifiers.add(PreemptiveMod.ID);
        }
        if (usesActions(SearingBlow.class) || callsMethod(AbstractCard.class, "upgrade")) {
            modifiers.add(AdaptiveMod.ID);
            modifiers.add(ArmedMod.ID);
            modifiers.add(DrainingMod.ID);
            modifiers.add(ExperiencedMod.ID);
            modifiers.add(FusedMod.ID);
            modifiers.add(SearingMod.ID);
        }
        if (callsMethod(AbstractOrb.class, "getRandomOrb")) {
            modifiers.add(ChaoticMod.ID);
        }
        if (callsMethod(CardRewardScreen.class, "customCombatOpen")) {
            modifiers.add(DiscoveredMod.ID);
            modifiers.add(SeekingMod.ID);
        }
        if (callsMethod(MonsterGroup.class, "getRandomMonster")) {
            modifiers.add(ErangMod.ID);
            modifiers.add(TearMod.ID);
            modifiers.add(TingshaMod.ID);
        }
        if (callsMethod(AbstractPlayer.class, "increaseMaxHp")) {
            modifiers.add(BrightestMod.ID);
            modifiers.add(FeedingMod.ID);
        }
        if (callsMethod(AbstractPlayer.class, "gainGold")) {
            modifiers.add(GoldenMod.ID);
            modifiers.add(GreedMod.ID);
        }
        if (callsMethod(AbstractPlayer.class, "gainEnergy")) {
            modifiers.add(DodecahedralMod.ID);
            modifiers.add(EnergizedMod.ID);
            modifiers.add(FollowUp.ID);
            modifiers.add(GuardedMod.ID);
            modifiers.add(HappyMod.ID);
            modifiers.add(HornedMod.ID);
            modifiers.add(NunchakuMod.ID);
            modifiers.add(RefluxingMod.ID);
            modifiers.add(ResidualMod.ID);
            modifiers.add(ShiningMod.ID);
            modifiers.add(StealthyMod.ID);
            modifiers.add(SozuMod.ID);
            modifiers.add(SplitMod.ID);
            modifiers.add(SunlitMod.ID);
            modifiers.add(TacticalMod.ID);
        }

        if (callsMethod(AbstractPlayer.class, "increaseMaxOrbSlots") || callsMethod(AbstractPlayer.class, "decreaseMaxOrbSlots")) {
            modifiers.add(CapaciousMod.ID);
            modifiers.add(ConsumingMod.ID);
            modifiers.add(InsertableMod.ID);
        }

        if (usesActions(ArtifactPower.class)) {
            modifiers.add(CleansingMod.ID);
            modifiers.add(IntrospectiveMod.ID);
            modifiers.add(SurgeMod.ID);
        }
        if (usesActions(BlockReturnPower.class)) {
            modifiers.add(HandMod.ID);
        }
        if (usesActions(BufferPower.class)) {
            modifiers.add(BufferedMod.ID);
        }
        if (usesActions(CardQueueItem.class)) {
            modifiers.add(AutoMod.ID);
            modifiers.add(BurstyMod.ID);
            modifiers.add(DisorderedMod.ID);
            modifiers.add(DivergentMod.ID);
            modifiers.add(DoublingMod.ID);
            modifiers.add(DuplicatingMod.ID);
            modifiers.add(EchoMod.ID);
            modifiers.add(GuilefulMod.ID);
            modifiers.add(LoudMod.ID);
            modifiers.add(TransfiguredMod.ID);
            modifiers.add(UnravelingMod.ID);
        }
        if (usesActions(ChangeStanceAction.class)) {
            modifiers.add(TemperamentalMod.ID);
        }
        if (usesActions(ChokePower.class)) {
            modifiers.add(ChokingMod.ID);
        }
        if (usesActions(CorpseExplosionPower.class)) {
            modifiers.add(ExplosionMod.ID);
        }
        if (usesActions(DamageAllEnemiesAction.class)) {
            modifiers.add(AshyMod.ID);
            modifiers.add(SharpMod.ID);
            modifiers.add(SluggerMod.ID);
        }
        if (usesActions(DamagePerAttackPlayedAction.class)) {
            modifiers.add(FinishingMod.ID);
        }
        if (usesActions(Dark.class)) {
            modifiers.add(GloomMod.ID);
        }
        if (usesActions(DexterityPower.class)) {
            modifiers.add(AnticipativeMod.ID);
            modifiers.add(DexterousMod.ID);
            modifiers.add(KunaiMod.ID);
            modifiers.add(SturdyMod.ID);
            modifiers.add(WearyMod.ID);
        }
        if (usesActions(DiscardPileToTopOfDeckAction.class)) {
            modifiers.add(HeadOnMod.ID);
        }
        if (usesActions(DoubleDamagePower.class)) {
            modifiers.add(PhantasmalMod.ID);
            modifiers.add(PointyMod.ID);
        }
        if (usesActions(DrawCardAction.class)) {
            modifiers.add(BackupMod.ID);
            modifiers.add(CubicMod.ID);
            modifiers.add(CutThroughMod.ID);
            modifiers.add(DarkMod.ID);
            modifiers.add(ExertedMod.ID);
            modifiers.add(ExpertMod.ID);
            modifiers.add(FastMod.ID);
            modifiers.add(FlashyMod.ID);
            modifiers.add(GamblerMod.ID);
            modifiers.add(HornedMod.ID);
            modifiers.add(ImpatientMod.ID);
            modifiers.add(InkyMod.ID);
            modifiers.add(LootableMod.ID);
            modifiers.add(MartialMod.ID);
            modifiers.add(OpportunisticMod.ID);
            modifiers.add(PagedMod.ID);
            modifiers.add(PreparedMod.ID);
            modifiers.add(ProactiveMod.ID);
            modifiers.add(QuickMod.ID);
            modifiers.add(ReflexMod.ID);
            modifiers.add(SanctifiedMod.ID);
            modifiers.add(ScribblyMod.ID);
            modifiers.add(SpitefulMod.ID);
            modifiers.add(StrategicMod.ID);
            modifiers.add(SuperluminalMod.ID);
            modifiers.add(UnceasingMod.ID);
            modifiers.add(WiseMod.ID);
        }
        if (usesActions(DrawCardNextTurnPower.class)) {
            modifiers.add(DoppelgangerMod.ID);
            modifiers.add(ExertedMod.ID);
            modifiers.add(FlowingMod.ID);
            modifiers.add(MementoMod.ID);
            modifiers.add(PocketwatchMod.ID);
            modifiers.add(PredatoryMod.ID);
            modifiers.add(UntappedMod.ID);
        }
        if (usesActions(EnlightenmentAction.class)) {
            modifiers.add(EnlightenedMod.ID);
        }
        if (usesActions(EmptyDeckShuffleAction.class)) {
            modifiers.add(DeepMod.ID);
            modifiers.add(TallyingMod.ID);
        }
        if (usesActions(EndTurnDeathPower.class)) {
            modifiers.add(BlasphemousMod.ID);
        }
        if (usesActions(EquilibriumPower.class)) {
            modifiers.add(EquilibrialMod.ID);
        }
        if (usesActions(FlechetteAction.class)) {
            modifiers.add(DartMod.ID);
        }
        if (usesActions(FocusPower.class)) {
            modifiers.add(AttentiveMod.ID);
            modifiers.add(FocusedMod.ID);
            modifiers.add(UnfocusedMod.ID);
        }
        if (usesActions(FrailPower.class)) {
            modifiers.add(IntrospectiveMod.ID);
            modifiers.add(ShamefulMod.ID);
        }
        if (usesActions(FreeAttackPower.class)) {
            modifiers.add(SwivelMod.ID);
        }
        if (usesActions(Frost.class)) {
            modifiers.add(FrostyMod.ID);
        }
        if (usesActions(HealAction.class)) {
            modifiers.add(BirdFacedMod.ID);
            modifiers.add(BitingMod.ID);
            modifiers.add(ReaperMod.ID);
        }
        if (usesActions(IntangiblePlayerPower.class)) {
            modifiers.add(AberrantMod.ID);
            modifiers.add(IncenseMod.ID);
        }
        if (usesActions(JudgementAction.class)) {
            modifiers.add(JudgeMod.ID);
        }
        if (usesActions(LessonLearnedAction.class)) {
            modifiers.add(LearnMod.ID);
        }
        if (usesActions(Lightning.class)) {
            modifiers.add(ElectroMod.ID);
            modifiers.add(StaticMod.ID);
        }
        if (usesActions(LockOnPower.class)) {
            modifiers.add(LockingMod.ID);
        }
        if (usesActions(LoseEnergyAction.class)) {
            modifiers.add(EnergizedMod.ID);
            modifiers.add(FastingMod.ID);
            modifiers.add(HastyMod.ID);
            modifiers.add(ResidualMod.ID);
            modifiers.add(VoidMod.ID);
            modifiers.add(VoidingMod.ID);
        }
        if (usesActions(LoseHPAction.class)) {
            modifiers.add(AchingMod.ID);
            modifiers.add(BerserkMod.ID);
            modifiers.add(BloodlettingMod.ID);
            modifiers.add(BloodyMod.ID);
            modifiers.add(CubicMod.ID);
            modifiers.add(DodecahedralMod.ID);
            modifiers.add(HemoMod.ID);
            modifiers.add(LapsingMod.ID);
            modifiers.add(MasterfulMod.ID);
            modifiers.add(RupturedMod.ID);
            modifiers.add(TungstenMod.ID);
            modifiers.add(VanishingMod.ID);
        }
        if (usesActions(MantraPower.class)) {
            modifiers.add(BrilliantMod.ID);
            modifiers.add(DevotedMod.ID);
            modifiers.add(PrayerfulMod.ID);
        }
        if (usesActions(MarkPower.class)) {
            modifiers.add(MarkedMod.ID);
        }
        if (usesActions(NextTurnBlockPower.class)) {
            modifiers.add(RollMod.ID);
        }
        if (usesActions(NoBlockPower.class)) {
            modifiers.add(FlusteredMod.ID);
            modifiers.add(PanicMod.ID);
        }
        if (usesActions(NoDrawPower.class)) {
            modifiers.add(TranceMod.ID);
        }
        if (usesActions(ObtainPotionAction.class)) {
            modifiers.add(LiquidizingMod.ID);
            modifiers.add(SozuMod.ID);
            modifiers.add(ThirstyMod.ID);
        }
        if (usesActions(Plasma.class)) {
            modifiers.add(MeteorMod.ID);
            modifiers.add(NuclearMod.ID);
        }
        if (usesActions(PressEndTurnButtonAction.class)) {
            modifiers.add(ConclusiveMod.ID);
            modifiers.add(TimeWarpedMod.ID);
        }
        if (usesActions(RagePower.class)) {
            modifiers.add(RagingMod.ID);
        }
        if (usesActions(RemoveAllBlockAction.class)) {
            modifiers.add(HeatingMod.ID);
            modifiers.add(ShatteringMod.ID);
        }
        if (usesActions(ScryAction.class)) {
            modifiers.add(CutThroughMod.ID);
            modifiers.add(LuckyMod.ID);
            modifiers.add(ThirdMod.ID);
        }
        if (usesActions(ShowCardAndAddToDrawPileEffect.class)) {
            modifiers.add(AlphaMod.ID);
            modifiers.add(EvolutionaryMod.ID);
            modifiers.add(ProudMod.ID);
            modifiers.add(VoidMod.ID);
        }
        if (usesActions(ShowCardAndAddToDiscardEffect.class)) {
            modifiers.add(AngryMod.ID);
        }
        if (usesActions(ShowCardAndAddToHandEffect.class)) {
            modifiers.add(BundledMod.ID);
            modifiers.add(CreativeMod.ID);
            modifiers.add(DeadMod.ID);
            modifiers.add(DistractingMod.ID);
            modifiers.add(EndlessMod.ID);
            modifiers.add(EntropicMod.ID);
            modifiers.add(ForeignMod.ID);
            modifiers.add(HelloMod.ID);
            modifiers.add(InescapableMod.ID);
            modifiers.add(InfernalMod.ID);
            modifiers.add(InfiniteMod.ID);
            modifiers.add(MagneticMod.ID);
            modifiers.add(NightmareMod.ID);
            modifiers.add(PredictiveMod.ID);
            modifiers.add(ReplaceableMod.ID);
            modifiers.add(ReplicativeMod.ID);
            modifiers.add(RhythmicMod.ID);
            modifiers.add(StormMod.ID);
        }
        if (usesActions(StrengthPower.class)) {
            modifiers.add(CultistMod.ID);
            modifiers.add(DisarmingMod.ID);
            modifiers.add(DuVuMod.ID);
            modifiers.add(FlexMod.ID);
            modifiers.add(HeavyMod.ID);
            modifiers.add(InflammableMod.ID);
            modifiers.add(ObservantMod.ID);
            modifiers.add(PhilosophersMod.ID);
            modifiers.add(RupturedMod.ID);
            modifiers.add(ShacklingMod.ID);
            modifiers.add(ShiftingMod.ID);
            modifiers.add(ShriekingMod.ID);
            modifiers.add(ShurikenMod.ID);
            modifiers.add(StrainedMod.ID);
            modifiers.add(SulfuricMod.ID);
            modifiers.add(TimeWarpedMod.ID);
        }
        if (usesActions(TheBombPower.class)) {
            modifiers.add(ExplosiveMod.ID);
        }
        if (usesActions(VigorPower.class)) {
            modifiers.add(RetributiveMod.ID);
            modifiers.add(UpliftingMod.ID);
            modifiers.add(WreathMod.ID);
        }
        if (usesActions(VulnerablePower.class)) {
            modifiers.add(AugerMod.ID);
            modifiers.add(BashMod.ID);
            modifiers.add(ChallengingMod.ID);
            modifiers.add(CruelMod.ID);
            modifiers.add(CrushingMod.ID);
            modifiers.add(FearfulMod.ID);
            modifiers.add(IntrospectiveMod.ID);
            modifiers.add(KickingMod.ID);
            modifiers.add(MarbleMod.ID);
            modifiers.add(ShockingMod.ID);
            modifiers.add(TerrifyingMod.ID);
            modifiers.add(UpperMod.ID);
        }
        if (usesActions(WeakPower.class)) {
            modifiers.add(CripplingMod.ID);
            modifiers.add(DeathbringingMod.ID);
            modifiers.add(DeceptiveMod.ID);
            modifiers.add(DoubtfulMod.ID);
            modifiers.add(HookedMod.ID);
            modifiers.add(IntimidatingMod.ID);
            modifiers.add(ShockingMod.ID);
            modifiers.add(SuckerMod.ID);
            modifiers.add(UpperMod.ID);
            modifiers.add(WavyMod.ID);
            modifiers.add(WhipMod.ID);
        }

        if (usesActions(DiscardAction.class, DiscardSpecificCardAction.class)) {
            modifiers.add(AllOutMod.ID);
            modifiers.add(ConcentratedMod.ID);
            modifiers.add(FlowingMod.ID);
            modifiers.add(GamblerMod.ID);
            modifiers.add(GraveMod.ID);
            modifiers.add(LootableMod.ID);
            modifiers.add(PreparedMod.ID);
            modifiers.add(SlipperyMod.ID);
            modifiers.add(StormMod.ID);
            modifiers.add(SurvivorMod.ID);
            modifiers.add(UnreliableMod.ID);
            modifiers.add(UnstableMod.ID);
        }
        if (usesActions(DrawPileToHandAction.class, SkillFromDeckToHandAction.class, AttackFromDeckToHandAction.class)) {
            modifiers.add(ClearMod.ID);
            modifiers.add(SecretMod.ID);
            modifiers.add(SeekingMod.ID);
            modifiers.add(ViolentMod.ID);
            modifiers.add(WaryMod.ID);
        }
        if (usesActions(MetallicizePower.class, PlatedArmorPower.class)) {
            modifiers.add(EternalMod.ID);
            modifiers.add(MetallicMod.ID);
            modifiers.add(PlatedMod.ID);
        }
        if (usesActions(BetterDiscardPileToHandAction.class, DiscardToHandAction.class)) {
            modifiers.add(AdaptiveMod.ID);
            modifiers.add(ForOneMod.ID);
            modifiers.add(HoloMod.ID);
        }
        if (usesActions(FlameBarrierPower.class, ThornsPower.class)) {
            modifiers.add(FlamingMod.ID);
            modifiers.add(ThornyMod.ID);
        }
        if (usesActions(EnergizedBluePower.class, EnergizedPower.class)) {
            modifiers.add(ArtOfMod.ID);
            modifiers.add(ChargedMod.ID);
            modifiers.add(CollectedMod.ID);
            modifiers.add(DoppelgangerMod.ID);
            modifiers.add(FlyingMod.ID);
            modifiers.add(SupplyMod.ID);
        }
        if (usesActions(ModifyDamageAction.class, ReduceCostAction.class, GashAction.class, ModifyBlockAction.class)) {
            modifiers.add(BracedMod.ID);
            modifiers.add(ClawfulMod.ID);
            modifiers.add(GlassKnife.ID);
            modifiers.add(IterativeMod.ID);
            modifiers.add(RampedMod.ID);
            modifiers.add(SteamMod.ID);
            modifiers.add(StreamlinedMod.ID);
        }
        if (usesActions(BlurPower.class, BarricadePower.class)) {
            modifiers.add(BlurryMod.ID);
        }
        if (usesActions(PoisonPower.class, BaneAction.class)) {
            modifiers.add(BanefulMod.ID);
            modifiers.add(CripplingMod.ID);
            modifiers.add(NoxiousMod.ID);
            modifiers.add(PerniciousMod.ID);
            modifiers.add(PoisonedMod.ID);
            modifiers.add(SpecimenMod.ID);
            modifiers.add(ToxicMod.ID);
        }
        if (usesActions(BarrageAction.class, ChannelAction.class)) {
            modifiers.add(BarrageMod.ID);
        }
        if (usesActions(EvokeAllOrbsAction.class, EvokeOrbAction.class, EvokeWithoutRemovingOrbAction.class, EvokeSpecificOrbAction.class)) {
            modifiers.add(CastMod.ID);
        }
        if (usesActions(RitualDaggerAction.class, IncreaseMiscAction.class)) {
            modifiers.add(GeneticMod.ID);
            modifiers.add(MoxieMod.ID);
        }

        if (usesActions(BloodForBlood.class)) {
            modifiers.add(BloodyMod.ID);
        }
        if (usesActions(BowlingBash.class)) {
            modifiers.add(BowlingMod.ID);
        }
        if (usesActions(Burn.class)) {
            modifiers.add(OverclockedMod.ID);
            modifiers.add(VolatileMod.ID);
        }
        if (usesActions(Clash.class)) {
            modifiers.add(ClashyMod.ID);
            modifiers.add(DemurMod.ID);
        }
        if (usesActions(Dazed.class)) {
            modifiers.add(RecklessMod.ID);
        }
        if (usesActions(ForceField.class)) {
            modifiers.add(ForcefulMod.ID);
        }
        if (usesActions(GrandFinale.class)) {
            modifiers.add(GrandMod.ID);
        }
        if (usesActions(HeavyBlade.class)) {
            modifiers.add(HeavyMod.ID);
        }
        if (usesActions(MasterfulStab.class)) {
            modifiers.add(MasterfulMod.ID);
        }
        if (usesActions(Miracle.class)) {
            modifiers.add(CollectedMod.ID);
            modifiers.add(HolyMod.ID);
        }
        if (usesActions(Shiv.class)) {
            modifiers.add(ShivMod.ID);
        }
        if (usesActions(SignatureMove.class)) {
            modifiers.add(SignatureMod.ID);
        }
        if (usesActions(Wound.class)) {
            modifiers.add(DeterminedMod.ID);
            modifiers.add(InjuriousMod.ID);
            modifiers.add(PainfulMod.ID);
        }
        if (usesActions(Smite.class, Safety.class)) {
            modifiers.add(RealityMod.ID);
        }

        cardToModifier.put(cardID, new ArrayList<>(modifiers));
    }
}
