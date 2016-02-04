package com.deathrayresearch.outlier.lang;

/**
 *
 */
public class Interpreter extends SmalltalkBaseListener {

  @Override
  public void enterStatementAnswer(SmalltalkParser.StatementAnswerContext ctx) {
    System.out.print("return the next value"); }

  @Override
  public void enterVariable(SmalltalkParser.VariableContext ctx) {
    System.out.print(ctx.IDENTIFIER()); }

  @Override
  public void enterAnswer(SmalltalkParser.AnswerContext ctx) {
    System.out.print(ctx.expression()); }

  @Override
  public void enterKeywordMessage(SmalltalkParser.KeywordMessageContext ctx) {
    System.out.print(ctx.keywordPair()); }

  @Override
  public void enterMessage(SmalltalkParser.MessageContext ctx) {
    System.out.print(ctx.binaryMessage()); }

}
