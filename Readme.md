Checkers
========
Java swing implementation of a checker board and checkers the game.

"Inspired" by http://www.javaworld.com/article/3014190/apis/checkers-anyone.html, specifically the horrible design followed in that article.

You can see that starting code at commit a4324a9ee8eda90fa96b1bc1609e5405f5ba6ce3, if you'd like.   But 2 commits later, the original code is completely gone, and the design shares nothing except the original idea and the grid layout starting at (1, 1) at the upper-left.

Design
======

Grid - (1,1) is the first square, and is in the upper-left corner.

Piece - abstracts away the "Checker" as the primary concern of a board.  It turned out that very little of the Board implementation needs to "know" it is dealing with a Checker.

Model vs. Presentation - separate the code between the logical game of checkers and the display of the board

Draw - the actual drawing of the piece delegated to PiecePaintStrategy interface.  Currently only PiecePaintStrategyCheckers is written, but Chess will be coming soon.


Comments on the Original
========================

On the plus-side - the code was reasonably well documented.  And, he has had enough experience to implement around the "public static final" crud in Java.  And he does follow the correct pattern for creating the GUI (i.e. invokeLater).  Also in his conclusion he does note that his PosCheck concept might be worthy of replacement.

On the not-so-good side -

Mixing game logic with display logic.  This was made clear when discovering the rules of checkers were not implemented, and looking at how hard it would be to implement those rules given that original framework.

AlreadyOccupiedException was a terrible design.  It looks more like the author was playing with Exceptions than implementing a useable API.

Coordinate system was confusing.  The code started out right in defining a (1,1) to (8,8) system, but then quickly dropped into "cx, cy" (which is the physical display center coordinates).

Drag-n-Drop was neat code, but needed to be better designed and encapsulated.  It was this item that prompted the re-write - because, why does a Board need to keep track of (inDrag, deltax, deltay, oldcx, oldcy)?  

CheckerType was a combination of concerns - regular/king and black/red.  This combination isn't too bad here, since 2+2 = 2*2.  But, if you map that idea into chess, the comparison becomes 6+2=8 versus 6*2=12

Java Issues
===========
The "KEY_ANTIALIASING" rendering hint seems to be completely broken in both  Java 7 and Java 8.  There is a 1-line "workaround" in BoardPainter.java.
