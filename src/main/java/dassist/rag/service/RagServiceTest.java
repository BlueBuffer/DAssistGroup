package dassist.rag.service;

public class RagServiceTest {

    public static void main(String[] args) {

        RagService ragService = new RagService();

        // 1. indexing
        ragService.index();

        // 2. ask question
        CitedAnswer result =
                ragService.ask("What is the side effects of insulin aspart"
                        + "?");

        System.out.println(result.getAnswer());

        int index = 1;
        for (Citation c : result.getCitations()) {
            System.out.println(
                    "[" + index + "] "
                            + c.getSource()
                            + " (" + c.getCategory() + ")"
            );
            index++;
        }
    }
}
