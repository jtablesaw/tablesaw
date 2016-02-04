// Generated from /Users/larrywhite/IdeaProjects/ColumnStorm/src/main/resources/Smalltalk.g42 by ANTLR 4.5
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SmalltalkParser}.
 */
public interface SmalltalkListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(SmalltalkParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(SmalltalkParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#sequence}.
	 * @param ctx the parse tree
	 */
	void enterSequence(SmalltalkParser.SequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#sequence}.
	 * @param ctx the parse tree
	 */
	void exitSequence(SmalltalkParser.SequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#ws}.
	 * @param ctx the parse tree
	 */
	void enterWs(SmalltalkParser.WsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#ws}.
	 * @param ctx the parse tree
	 */
	void exitWs(SmalltalkParser.WsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#temps}.
	 * @param ctx the parse tree
	 */
	void enterTemps(SmalltalkParser.TempsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#temps}.
	 * @param ctx the parse tree
	 */
	void exitTemps(SmalltalkParser.TempsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StatementAnswer}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatementAnswer(SmalltalkParser.StatementAnswerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StatementAnswer}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatementAnswer(SmalltalkParser.StatementAnswerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StatementExpressionsAnswer}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpressionsAnswer(SmalltalkParser.StatementExpressionsAnswerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StatementExpressionsAnswer}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpressionsAnswer(SmalltalkParser.StatementExpressionsAnswerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StatementExpressions}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpressions(SmalltalkParser.StatementExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StatementExpressions}
	 * labeled alternative in {@link SmalltalkParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpressions(SmalltalkParser.StatementExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#answer}.
	 * @param ctx the parse tree
	 */
	void enterAnswer(SmalltalkParser.AnswerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#answer}.
	 * @param ctx the parse tree
	 */
	void exitAnswer(SmalltalkParser.AnswerContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(SmalltalkParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(SmalltalkParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(SmalltalkParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(SmalltalkParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(SmalltalkParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(SmalltalkParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#cascade}.
	 * @param ctx the parse tree
	 */
	void enterCascade(SmalltalkParser.CascadeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#cascade}.
	 * @param ctx the parse tree
	 */
	void exitCascade(SmalltalkParser.CascadeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(SmalltalkParser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(SmalltalkParser.MessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(SmalltalkParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(SmalltalkParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(SmalltalkParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(SmalltalkParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#binarySend}.
	 * @param ctx the parse tree
	 */
	void enterBinarySend(SmalltalkParser.BinarySendContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#binarySend}.
	 * @param ctx the parse tree
	 */
	void exitBinarySend(SmalltalkParser.BinarySendContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#unarySend}.
	 * @param ctx the parse tree
	 */
	void enterUnarySend(SmalltalkParser.UnarySendContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#unarySend}.
	 * @param ctx the parse tree
	 */
	void exitUnarySend(SmalltalkParser.UnarySendContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#keywordSend}.
	 * @param ctx the parse tree
	 */
	void enterKeywordSend(SmalltalkParser.KeywordSendContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#keywordSend}.
	 * @param ctx the parse tree
	 */
	void exitKeywordSend(SmalltalkParser.KeywordSendContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#keywordMessage}.
	 * @param ctx the parse tree
	 */
	void enterKeywordMessage(SmalltalkParser.KeywordMessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#keywordMessage}.
	 * @param ctx the parse tree
	 */
	void exitKeywordMessage(SmalltalkParser.KeywordMessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#keywordPair}.
	 * @param ctx the parse tree
	 */
	void enterKeywordPair(SmalltalkParser.KeywordPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#keywordPair}.
	 * @param ctx the parse tree
	 */
	void exitKeywordPair(SmalltalkParser.KeywordPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#operand}.
	 * @param ctx the parse tree
	 */
	void enterOperand(SmalltalkParser.OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#operand}.
	 * @param ctx the parse tree
	 */
	void exitOperand(SmalltalkParser.OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#subexpression}.
	 * @param ctx the parse tree
	 */
	void enterSubexpression(SmalltalkParser.SubexpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#subexpression}.
	 * @param ctx the parse tree
	 */
	void exitSubexpression(SmalltalkParser.SubexpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(SmalltalkParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(SmalltalkParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#runtimeLiteral}.
	 * @param ctx the parse tree
	 */
	void enterRuntimeLiteral(SmalltalkParser.RuntimeLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#runtimeLiteral}.
	 * @param ctx the parse tree
	 */
	void exitRuntimeLiteral(SmalltalkParser.RuntimeLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(SmalltalkParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(SmalltalkParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#blockParamList}.
	 * @param ctx the parse tree
	 */
	void enterBlockParamList(SmalltalkParser.BlockParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#blockParamList}.
	 * @param ctx the parse tree
	 */
	void exitBlockParamList(SmalltalkParser.BlockParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#dynamicDictionary}.
	 * @param ctx the parse tree
	 */
	void enterDynamicDictionary(SmalltalkParser.DynamicDictionaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#dynamicDictionary}.
	 * @param ctx the parse tree
	 */
	void exitDynamicDictionary(SmalltalkParser.DynamicDictionaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#dynamicArray}.
	 * @param ctx the parse tree
	 */
	void enterDynamicArray(SmalltalkParser.DynamicArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#dynamicArray}.
	 * @param ctx the parse tree
	 */
	void exitDynamicArray(SmalltalkParser.DynamicArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#parsetimeLiteral}.
	 * @param ctx the parse tree
	 */
	void enterParsetimeLiteral(SmalltalkParser.ParsetimeLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#parsetimeLiteral}.
	 * @param ctx the parse tree
	 */
	void exitParsetimeLiteral(SmalltalkParser.ParsetimeLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SmalltalkParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SmalltalkParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#numberExp}.
	 * @param ctx the parse tree
	 */
	void enterNumberExp(SmalltalkParser.NumberExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#numberExp}.
	 * @param ctx the parse tree
	 */
	void exitNumberExp(SmalltalkParser.NumberExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#charConstant}.
	 * @param ctx the parse tree
	 */
	void enterCharConstant(SmalltalkParser.CharConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#charConstant}.
	 * @param ctx the parse tree
	 */
	void exitCharConstant(SmalltalkParser.CharConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#hex}.
	 * @param ctx the parse tree
	 */
	void enterHex(SmalltalkParser.HexContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#hex}.
	 * @param ctx the parse tree
	 */
	void exitHex(SmalltalkParser.HexContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#stInteger}.
	 * @param ctx the parse tree
	 */
	void enterStInteger(SmalltalkParser.StIntegerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#stInteger}.
	 * @param ctx the parse tree
	 */
	void exitStInteger(SmalltalkParser.StIntegerContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#stFloat}.
	 * @param ctx the parse tree
	 */
	void enterStFloat(SmalltalkParser.StFloatContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#stFloat}.
	 * @param ctx the parse tree
	 */
	void exitStFloat(SmalltalkParser.StFloatContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#pseudoVariable}.
	 * @param ctx the parse tree
	 */
	void enterPseudoVariable(SmalltalkParser.PseudoVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#pseudoVariable}.
	 * @param ctx the parse tree
	 */
	void exitPseudoVariable(SmalltalkParser.PseudoVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(SmalltalkParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(SmalltalkParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#symbol}.
	 * @param ctx the parse tree
	 */
	void enterSymbol(SmalltalkParser.SymbolContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#symbol}.
	 * @param ctx the parse tree
	 */
	void exitSymbol(SmalltalkParser.SymbolContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#primitive}.
	 * @param ctx the parse tree
	 */
	void enterPrimitive(SmalltalkParser.PrimitiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#primitive}.
	 * @param ctx the parse tree
	 */
	void exitPrimitive(SmalltalkParser.PrimitiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#bareSymbol}.
	 * @param ctx the parse tree
	 */
	void enterBareSymbol(SmalltalkParser.BareSymbolContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#bareSymbol}.
	 * @param ctx the parse tree
	 */
	void exitBareSymbol(SmalltalkParser.BareSymbolContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#literalArray}.
	 * @param ctx the parse tree
	 */
	void enterLiteralArray(SmalltalkParser.LiteralArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#literalArray}.
	 * @param ctx the parse tree
	 */
	void exitLiteralArray(SmalltalkParser.LiteralArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#literalArrayRest}.
	 * @param ctx the parse tree
	 */
	void enterLiteralArrayRest(SmalltalkParser.LiteralArrayRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#literalArrayRest}.
	 * @param ctx the parse tree
	 */
	void exitLiteralArrayRest(SmalltalkParser.LiteralArrayRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#bareLiteralArray}.
	 * @param ctx the parse tree
	 */
	void enterBareLiteralArray(SmalltalkParser.BareLiteralArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#bareLiteralArray}.
	 * @param ctx the parse tree
	 */
	void exitBareLiteralArray(SmalltalkParser.BareLiteralArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#unaryTail}.
	 * @param ctx the parse tree
	 */
	void enterUnaryTail(SmalltalkParser.UnaryTailContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#unaryTail}.
	 * @param ctx the parse tree
	 */
	void exitUnaryTail(SmalltalkParser.UnaryTailContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#unaryMessage}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMessage(SmalltalkParser.UnaryMessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#unaryMessage}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMessage(SmalltalkParser.UnaryMessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#unarySelector}.
	 * @param ctx the parse tree
	 */
	void enterUnarySelector(SmalltalkParser.UnarySelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#unarySelector}.
	 * @param ctx the parse tree
	 */
	void exitUnarySelector(SmalltalkParser.UnarySelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#keywords}.
	 * @param ctx the parse tree
	 */
	void enterKeywords(SmalltalkParser.KeywordsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#keywords}.
	 * @param ctx the parse tree
	 */
	void exitKeywords(SmalltalkParser.KeywordsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(SmalltalkParser.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(SmalltalkParser.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#binaryTail}.
	 * @param ctx the parse tree
	 */
	void enterBinaryTail(SmalltalkParser.BinaryTailContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#binaryTail}.
	 * @param ctx the parse tree
	 */
	void exitBinaryTail(SmalltalkParser.BinaryTailContext ctx);
	/**
	 * Enter a parse tree produced by {@link SmalltalkParser#binaryMessage}.
	 * @param ctx the parse tree
	 */
	void enterBinaryMessage(SmalltalkParser.BinaryMessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link SmalltalkParser#binaryMessage}.
	 * @param ctx the parse tree
	 */
	void exitBinaryMessage(SmalltalkParser.BinaryMessageContext ctx);
}