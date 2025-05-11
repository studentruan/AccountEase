public class MergeMainForExample {

    /**
     * Example main method to merge transaction XML files from a source folder into a custom output file.
     */
    public static void main(String[] args) {
        // Set input folder and output file path
        String inputFolderPath = "C:/Users/ASUS/Desktop/XML";  // Folder that contains multiple .xml files
        String outputFilePath = "C:/Users/ASUS/Desktop/Merged/merged_transactions.xml";  // Final merged file path

        // Call the merge utility
        String result = TransactionXmlMerger.mergeTransactionXmlFiles(inputFolderPath, outputFilePath);

        // Output result
        if (result != null) {
            System.out.println("XML merge successful!");
            System.out.println("Merged file saved at: " + result);
        } else {
            System.out.println("XML merge failed. Please check input folder or file format.");
        }
    }
}
