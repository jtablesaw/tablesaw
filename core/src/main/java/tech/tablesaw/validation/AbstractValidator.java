package tech.tablesaw.validation;

public abstract class AbstractValidator<T> implements Validator<T> {

  private String name;

  public AbstractValidator(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public AbstractValidator<T> setName(String s) {
    name = s;
    return this;
  }
}
