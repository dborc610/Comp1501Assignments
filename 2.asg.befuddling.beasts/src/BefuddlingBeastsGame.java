import java.util.Scanner;

public class BefuddlingBeastsGame {

    private static final int INITIAL_COLOR_POOL_SIZE = 10;
    private static final int INITIAL_BEAST_HAND_SIZE = 3;
    private static final int TARGET_ENDING_SCORE = 30;

    private boolean userWishesToContinue = true;

    private Scorer scorer;
    private Scanner kbd = new Scanner(System.in);
    private MenuValidator menuValidator;
    private ColorPool colorPool;
    private ColorPoolView colorPoolView;
    private Hand hand;
    private HandView handView;
    private BeastDeck beastDeck;
    private ColorDeck colorDeck;
    private PlayAreaView playAreaView;
    private CurrentScoreView currentScoreView;
    private FinalScoreView finalScoreView;

    public BefuddlingBeastsGame() {
	this.kbd = new Scanner(System.in);
	this.menuValidator = new MenuValidator(kbd, "PDQ");
	this.colorPool = new ColorPool();
	this.colorPoolView = new ColorPoolView(colorPool);
	this.hand = new Hand();
	this.handView = new HandView(hand);
	this.beastDeck = new BeastDeck();
	this.colorDeck = new ColorDeck();
	this.scorer = new Scorer(TARGET_ENDING_SCORE, hand);
	this.currentScoreView = new CurrentScoreView(scorer);
	this.playAreaView = new PlayAreaView(colorPoolView, currentScoreView, handView);
	this.finalScoreView = new FinalScoreView(scorer);
    }

    public void run() {

	drawInitialBeastHand();
	fillInitilaColorPool();

	String response;

	do {
	    System.out.println(playAreaView.view());
	    response = menuValidator.validSelection("Your choice? ");
	    System.out.println();
	    handle(response);
	} while (gameNeedsToContinue());

	displayFinalScore();
	displayThanks();
    }

    // TODO
    //
    // Should return true if the game should continue - that is,
    // if the user wishes to keep playing and the game ending
    // score has not been reached.
    //
    private boolean gameNeedsToContinue() {
	return userWishesToContinue && !scorer.gameEndingScoreReached();
    }


    // TODO
    //
    // Should fill the color pool with the correct number
    // of colors.
    //
    private void fillInitilaColorPool() {
	for (int i = 0; i < INITIAL_COLOR_POOL_SIZE; i++) {
	    colorPool.gain(colorDeck.draw());
	}
    }

    // TODO
    //
    // Should draw the starting number of cards from the beast deck and add
    // them to the hand.
    //
    private void drawInitialBeastHand() {
	for (int i = 0; i < INITIAL_BEAST_HAND_SIZE; i++) {
	    hand.add(beastDeck.drawnCard());
	}
    }

    private void displayThanks() {
	System.out.println();
	System.out.println("Thank you for playing!");
	System.out.println();
    }

    private void handle(String response) {
	switch (response) {
	    case "P":
		handlePlayCard();
		break;
	    case "D":
		handleDrawCard();
		break;
	    case "Q":
		handleQuit();
		break;
	    default:
		handleUnknownResponse();
		break;
	}
    }

    private void handleDrawCard() {
	System.out.println("Drawing...");
	gainColor();
	drawBeastCard();
    }

    private void drawBeastCard() {
	BeastCard card = beastDeck.drawnCard();
	System.out.println("Drawn beast: " + card);
	System.out.println();

	hand.add(card);
    }

    private void handleUnknownResponse() {
	System.out.println("How did you even GET here?!?");
    }

    private void handleQuit() {
	userWishesToContinue = false;
    }

    private void displayFinalScore() {
	System.out.println();
	System.out.println(finalScoreView.view());
	System.out.println();
    }

    private void handlePlayCard() {
	if (!hand.hasPlayableCard(colorPool)) {
	    System.out.println("You have no playable card.");
	} else {
	    playCard();
	}

    }

    private void playCard() {

	BetweenValidator cardChoiceValidator = new BetweenValidator(kbd, 1, hand.size());
	int desiredCardNum = cardChoiceValidator.validInclusiveNumber("Which card? ");

	int desiredCardIndex = desiredCardNum - 1;
	BeastCard cardToPlay = hand.get(desiredCardIndex);
	while (!colorPool.canPayFor(cardToPlay)) {
	    System.out.println("Don't have enough mana to play that card.");
	    desiredCardNum = cardChoiceValidator.validInclusiveNumber("Which card? ");
	    desiredCardIndex = desiredCardNum - 1;
	    cardToPlay = hand.get(desiredCardIndex);
	}
	scorer.updateScore(cardToPlay);
	removeCardFromHand(desiredCardIndex);
    }


    private void removeCardFromHand(int index) {
	colorPool.pay(hand.get(index).cost());
	hand.remove(index);
    }

    private void gainColor() {
	String color = colorDeck.draw();
	System.out.println("Drawn color: " + color);
	colorPool.gain(color);
    }




}
