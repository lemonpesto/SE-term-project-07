package view;

public interface ISetupView {
    void setSetupViewListener(ISetupViewListener listener);

    void showView();

    void closeView();
}