package solution;

import resources.classes.TextIO;



public class ModifiedSimpleParser3V2 {


    //    -------------------- Nested classes for Expression Trees ------------------------------
    /**
     *  An abstract class representing any node in an expression tree.
     *  The three concrete node classes are concrete subclasses.
     *  Two instance methods are specified, so that they can be used with
     *  any ExpNode.  The value() method returns the value of the
     *  expression.  The printStackCommands() method prints a list
     *  of commands that could be used to evaluate the expression on
     *  a stack machine (assuming that the value of the expression is
     *  to be left on the stack).
     */
    abstract private static class ExpNode {
        abstract double value(double xVal);
        abstract void printStackCommands();
        abstract ExpNode derivative();
    }

    /**
     * Represents an expression node that holds a number.
     */
    private static class ConstNode extends ExpNode {
        double number;  // The number.
//        double derivative;
        ConstNode(double val) {
            // Construct a ConstNode containing the specified number.
            number = val;
        }
        double value(double xVal) {
            // The value of the node is the number that it contains.
            return number;
        }

        ConstNode derivative() {
            return new ConstNode(0) {
            };
        }

        void printStackCommands() {
            // On a stack machine, just push the number onto the stack.
            System.out.println("  Push " + number);
        }

        public String toString (){
            return String.valueOf(number);
        }

    }

    /**
     * An expression node representing a binary operator.
     */
    private static class BinOpNode extends ExpNode {
        double xVal;
        char op;        // The operator.
        ExpNode left;   // The expression for its left operand.
        ExpNode right;  // The expression for its right operand.
        BinOpNode(char op, ExpNode left, ExpNode right) {
            // Construct a BinOpNode containing the specified data.
            assert op == '+' || op == '-' || op == '*' || op == '/';
            assert left != null && right != null;
            this.op = op;
            this.left = left;
            this.right = right;
        }
        double value(double xVal) {
            // The value is obtained by evaluating the left and right
            // operands and combining the values with the operator.
            this.xVal = xVal;
            double x = left.value(xVal);
            double y = right.value(xVal);
            switch (op) {
                case '+':  return x + y;
                case '-':  return x - y;
                case '*':  return x * y;
                case '/':  return x / y;
                default:   return Double.NaN;  // Bad operator!
            }
        }

        ExpNode derivative (){
            ExpNode A = left;
            ExpNode B = right;
            ExpNode dA = left.derivative();
            ExpNode dB = right.derivative();

            switch (op) {
                case '+': {
                    return new BinOpNode('+', dA, dB);
                        //• If A and B are expressions, let dA be the derivative of A and let dB be the derivative
                        //                    of B. Then the derivative of A+B is dA+dB.
                }
                case '-': {
                    return new BinOpNode('-', dA,dB);
//                    • The derivative of A-B is dA-dB.
                }
                case '*':{
                    BinOpNode newA = new BinOpNode('*', A, dB);
                    BinOpNode newB = new BinOpNode('*', B, dA);
                    return new BinOpNode('+', newA,newB);
//                    • The derivative of A*B is A*dB + B*dA.

                }
                case '/':{
                    BinOpNode newA = new BinOpNode('*', A, dB);
                    BinOpNode newB = new BinOpNode('*', B, dA);
                    BinOpNode numerator = new BinOpNode('-', newA,newB);
                    BinOpNode denominator = new BinOpNode('*',B,B);
                    return new BinOpNode('/', numerator, denominator);
//                    • The derivative of A/B is (B*dA - A*dB) / (B*B).

                }
                default:   return  new BinOpNode('+', A,B);  // Bad operator!
            }
        }

        void  printStackCommands() {
            // To evaluate the expression on a stack machine, first do
            // whatever is necessary to evaluate the left operand, leaving
            // the answer on the stack.  Then do the same thing for the
            // second operand.  Then apply the operator (which means popping
            // the operands, applying the operator, and pushing the result).
            left.printStackCommands();
            right.printStackCommands();
            System.out.println("  Operator " + op);
        }

        public String toString(){
            return "( " + left.toString() +" "+ op +" " + right.toString() + " )";
        }

    }

    private static class variableNode extends ExpNode{
        double xVal;

        variableNode(){
        }

        double value(double xVal) {
            this.xVal = xVal;
            return xVal;
        }

        ExpNode derivative() {
            return new ConstNode(1);
        }

        void printStackCommands() {
            System.out.println(" Value of x is " + value(xVal));
        }

        public String toString (){
            return "X";
        }

    }


    /**
     * An expression node to represent a unary minus operator.
     */
    private static class UnaryMinusNode extends ExpNode {
        ExpNode operand;  // The operand to which the unary minus applies.
        UnaryMinusNode(ExpNode operand) {
            // Construct a UnaryMinusNode with the specified operand.
            assert operand != null;
            this.operand = operand;
        }
        double value(double xVal) {
            // The value is the negative of the value of the operand.
            double neg = operand.value(xVal);
            return -neg;
        }

        ExpNode derivative (){
            return new UnaryMinusNode(operand.derivative());
//            If A is an expression, let dA be the derivative of A. Then the derivative of -A is -dA.
        }
        void printStackCommands() {
            // To evaluate this expression on a stack machine, first do
            // whatever is necessary to evaluate the operand, leaving the
            // operand on the stack.  Then apply the unary minus (which means
            // popping the operand, negating it, and pushing the result).
            operand.printStackCommands();
            System.out.println("  Unary minus");
        }

        public String toString (){
            return "-" + operand.toString();
        }
    }


    //    -------------------------------------------------------------------------------


    /**
     * An object of type ParseError represents a syntax error found in
     * the user's input.
     */
    private static class ParseError extends Exception {
        ParseError(String message) {
            super(message);
        }
    } // end nested class ParseError


    public static void main(String[] args) {

        while (true) {
            System.out.println("\n\nEnter an expression, or press return to end.");
            System.out.print("\n?  ");
            TextIO.skipBlanks();
            if ( TextIO.peek() == '\n' )
                break;
            try {
                ExpNode exp = expressionTree();
                TextIO.skipBlanks();
                if ( TextIO.peek() != '\n' )
                    throw new ParseError("Extra data after end of expression.");
                TextIO.getln();

//                System.out.println("\nValue is ");
                exp.derivative().printStackCommands();
                System.out.println("\nValue is " + exp.derivative().toString());
//                System.out.println("\nValue is " + exp.derivative());
//                System.out.println("\nValue is " + exp.derivative());
//                System.out.println("\nOrder of postfix evaluation is:\n");
//                exp.printStackCommands();
            }
            catch (ParseError e) {
                System.out.println("\n*** Error in input:    " + e.getMessage());
                System.out.println("*** Discarding input:  " + TextIO.getln());
            }
        }

        System.out.println("\n\nDone.");

    } // end main()


    /**
     * Reads an expression from the current line of input and builds
     * an expression tree that represents the expression.
     * @return an ExpNode which is a pointer to the root node of the
     *    expression tree
     * @throws ParseError if a syntax error is found in the input
     */
    private static ExpNode expressionTree() throws ParseError {
        TextIO.skipBlanks();
        boolean negative;  // True if there is a leading minus sign.
        negative = false;
        if (TextIO.peek() == '-') {
            TextIO.getAnyChar();
            negative = true;
        }
        ExpNode exp;       // The expression tree for the expression.
        exp = termTree();  // Start with the first term.
        if (negative)
            exp = new UnaryMinusNode(exp);
        TextIO.skipBlanks();
        while ( TextIO.peek() == '+' || TextIO.peek() == '-' ) {
            // Read the next term and combine it with the
            // previous terms into a bigger expression tree.
            char op = TextIO.getAnyChar();
            ExpNode nextTerm = termTree();
            exp = new BinOpNode(op, exp, nextTerm);
            TextIO.skipBlanks();
        }
        return exp;
    } // end expressionTree()


    /**
     * Reads a term from the current line of input and builds
     * an expression tree that represents the expression.
     * @return an ExpNode which is a pointer to the root node of the
     *    expression tree
     * @throws ParseError if a syntax error is found in the input
     */
    private static ExpNode termTree() throws ParseError {
        TextIO.skipBlanks();
        ExpNode term;  // The expression tree representing the term.
        term = factorTree();
        TextIO.skipBlanks();
        while ( TextIO.peek() == '*' || TextIO.peek() == '/' ) {
            // Read the next factor, and combine it with the
            // previous factors into a bigger expression tree.
            char op = TextIO.getAnyChar();
            ExpNode nextFactor = factorTree();
            term = new BinOpNode(op,term,nextFactor);
            TextIO.skipBlanks();
        }
        return term;
    } // end termValue()


    /**
     * Reads a factor from the current line of input and builds
     * an expression tree that represents the expression.
     * @return an ExpNode which is a pointer to the root node of the
     *    expression tree
     * @throws ParseError if a syntax error is found in the input
     */
    private static ExpNode factorTree() throws ParseError {
        TextIO.skipBlanks();
        char ch = TextIO.peek();
        if ( Character.isDigit(ch) ) {
            // The factor is a number.  Return a ConstNode.
            double num = TextIO.getDouble();
            return new ConstNode(num);
        }
        else if( ch == 'x'|| ch == 'X' ){
            TextIO.getAnyChar();//read the "x"
            return new variableNode();
        }
        else if ( ch == '(' ) {
            // The factor is an expression in parentheses.
            // Return a tree representing that expression.
            TextIO.getAnyChar();  // Read the "("
            ExpNode exp = expressionTree();
            TextIO.skipBlanks();
            if ( TextIO.peek() != ')' )
                throw new ParseError("Missing right parenthesis.");
            TextIO.getAnyChar();  // Read the ")"
            return exp;
        }
        else if ( ch == '\n' )
            throw new ParseError("End-of-line encountered in the middle of an expression.");
        else if ( ch == ')' )
            throw new ParseError("Extra right parenthesis.");
        else if ( ch == '+' || ch == '-' || ch == '*' || ch == '/' )
            throw new ParseError("Misplaced operator.");
        else
            throw new ParseError("Unexpected character \"" + ch + "\" encountered.");
    }  // end factorTree()


} // end class SimpleParser3

