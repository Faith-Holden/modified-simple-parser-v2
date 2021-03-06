# modified-simple-parser-v2
My solution for Chapter 9 Exercise 7 of “Introduction to Programming Using Java”.
Implementation notes: This is an update of the modified-simple-parser program from 9.6.
This version includes functionality for taking derivatives. As such, I modified the nested
classes to include a derivative() method, and modified the main class accordingly.


Problem Description:
This exercise builds on the previous exercise, Exercise 9.6. To understand it, you should
have some background in Calculus. The derivative of an expression that involves the
variable x can be defined by a few recursive rules:
• The derivative of a constant is 0.
• The derivative of x is 1.
• If A is an expression, let dA be the derivative of A. Then the derivative of -A is -dA.
• If A and B are expressions, let dA be the derivative of A and let dB be the derivative
of B. Then the derivative of A+B is dA+dB.
• The derivative of A-B is dA-dB.
• The derivative of A*B is A*dB + B*dA.
• The derivative of A/B is (B*dA - A*dB) / (B*B).
For this exercise, you should modify your program from the previous exercise so that
it can compute the derivative of an expression. You can do this by adding a derivative-
computing method to each of the node classes. First, add another abstract method to the
ExpNode class:
abstract ExpNode derivative();
Then implement this method in each of the four subclasses of ExpNode. All the information
that you need is in the rules given above. In your main program, instead of printing the
stack operations for the original expression, you should print out the stack operations
that define the derivative. Note that the formula that you get for the derivative can be
much more complicated than it needs to be. For example, the derivative of 3*x+1 will be
computed as (3*1+0*x)+0. This is correct, even though it’s kind of ugly, and it would be
nice for it to be simplified. However, simplifying expressions is not easy.
As an alternative to printing out stack operations, you might want to print the deriva-
tive as a fully parenthesized expression. You can do this by adding a printInfix() routine
to each node class. It would be nice to leave out unnecessary parentheses, but again, the
problem of deciding which parentheses can be left out without altering the meaning of the
expression is a fairly difficult one, which I don’t advise you to attempt.
(There is one curious thing that happens here: If you apply the rules, as given, to an
expression tree, the result is no longer a tree, since the same subexpression can occur at
multiple points in the derivative. For example, if you build a node to represent B*B by
saying “new BinOpNode(’*’,B,B)”, then the left and right children of the new node are
actually the same node! This is not allowed in a tree. However, the difference is harmless
in this case since, like a tree, the structure that you get has no loops in it. Loops, on the
other hand, would be a disaster in most of the recursive tree-processing subroutines that
we have written, since it would lead to infinite recursion. The type of structure that is
built by the derivative functions is technically referred to as a directed acyclic graph.)

Notes: TextIO is an IO helper class created by the textbook's author to assist with IO before the book covers java's IO utilities.
