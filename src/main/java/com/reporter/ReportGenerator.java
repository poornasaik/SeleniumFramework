package com.reporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReportGenerator {

    private String[] methodNames;
    private int[] passCount;
    private int[] failCount;
    private int[] totalCount;

    public ReportGenerator(String[] methodNames, int[] passCount, int[] failCount, int[] totalCount) {
        this.methodNames = methodNames;
        this.passCount = passCount;
        this.failCount = failCount;
        this.totalCount = totalCount;
    }

    public void generateReport(String fileName) throws IOException {
        File file = new File(fileName);
        PrintWriter writer = new PrintWriter(new FileWriter(file));

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>");
        writer.println("<script type=\"text/javascript\">");
        writer.println("google.charts.load('current', {'packages':['corechart']});");
        writer.println("google.charts.setOnLoadCallback(drawChart);");
        writer.println("function drawChart() {");
        writer.println("var data = new google.visualization.DataTable();");
        writer.println("data.addColumn('string', 'Method');");
        writer.println("data.addColumn('number', 'Pass');");
        writer.println("data.addColumn('number', 'Fail');");
        writer.println("data.addRows([");
        
        for (int i = 0; i < methodNames.length; i++) {
            double passPercentage = (double) passCount[i] / totalCount[i] * 100;
            double failPercentage = (double) failCount[i] / totalCount[i] * 100;
            writer.println("['" + methodNames[i] + "', " + passPercentage + ", " + failPercentage + "],");
        }
        
        writer.println("]);");
        writer.println("var options = {");
        writer.println("title: 'Test Results',");
        writer.println("is3D: true");
        writer.println("};");
        writer.println("var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));");
        writer.println("chart.draw(data, options);");
        writer.println("}");
        writer.println("</script>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("<div id=\"piechart_3d\" style=\"width: 900px; height: 500px;\"></div>");
        writer.println("</body>");
        writer.println("</html>");

        writer.close();
    }
}
