package com.reporter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportGenerator2 {

    private String[] methodNames;
    private int[] passCount;
    private int[] failCount;
    private int[] totalCount;

    public ReportGenerator2(String[] methodNames, int[] passCount, int[] failCount, int[] totalCount) {
        this.methodNames = methodNames;
        this.passCount = passCount;
        this.failCount = failCount;
        this.totalCount = totalCount;
    }

    public void generateReport(String fileName) throws IOException {
        StringBuilder html = new StringBuilder();

        // HTML head
        html.append("<!DOCTYPE html>\n<html>\n<head>\n<title>Test Results</title>\n");
        html.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
        html.append("</head>\n<body>\n");

        // Table
        html.append("<table>\n<thead>\n<tr><th>Method Name</th><th>Pass Count</th><th>Fail Count</th><th>Total Count</th></tr>\n</thead>\n<tbody>\n");

        // Add table rows for each method
        for (int i = 0; i < methodNames.length; i++) {
            html.append("<tr><td>").append(methodNames[i]).append("</td>");
            html.append("<td>").append(passCount[i]).append("</td>");
            html.append("<td>").append(failCount[i]).append("</td>");
            html.append("<td>").append(totalCount[i]).append("</td></tr>\n");
        }

        html.append("</tbody>\n</table>\n");

        // Chart canvas
        html.append("<canvas id=\"chart\"></canvas>\n");

        // JavaScript for creating the pie chart
        html.append("<script>\n");
        html.append("var chartData = {\n");
        html.append("labels: [");

        // Add method names as chart labels
        for (int i = 0; i < methodNames.length; i++) {
            html.append("\"").append(methodNames[i]).append("\"");
            if (i < methodNames.length - 1) {
                html.append(",");
            }
        }

        html.append("],\n");
        html.append("datasets: [{\n");
        html.append("data: [");

        // Add pass/fail percentages as chart data
        for (int i = 0; i < methodNames.length; i++) {
            double passPercentage = (double) passCount[i] / totalCount[i] * 100;
            double failPercentage = (double) failCount[i] / totalCount[i] * 100;

            html.append(passPercentage).append(",").append(failPercentage);
            if (i < methodNames.length - 1) {
                html.append(",");
            }
        }

        html.append("],\n");
        html.append("backgroundColor: [\"green\", \"red\"]\n");
        html.append("}]\n");
        html.append("};\n");

        html.append("var ctx = document.getElementById('chart').getContext('2d');\n");
        html.append("var myChart = new Chart(ctx, {\n");
        html.append("type: 'pie',\n");
        html.append("data: chartData\n");
        html.append("});\n");
        html.append("</script>\n");

        html.append("</body>\n</html>");

        FileWriter writer = new FileWriter(fileName);
        writer.write(html.toString());
        writer.close();
    }
}
