<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cucumber Test Report</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
            color: #333; /* Text color for the body */
        }

        table {
            font-size: 14px;
            border-collapse: collapse;
            width: 100%;
            margin-top: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* Box shadow for the table */
            overflow: hidden;
            border-radius: 8px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }

        th {
            background-color: #1d00c6;
            color: white;
            font-weight: bold; /* Added bold font weight for header cells */
        }

        .passed {
            color: #4CAF50;
            font-weight: bold;
        }

        .failed {
            color: #FF5252;
            font-weight: bold;
        }

        .skipped {
            color: #FFC107; /* New color for skipped status */
            font-weight: bold;
        }

        .example-label {
            color: #ffb100; /* Dark Green for Example: text */
            font-weight: bold;
        }

        .example-value {
            color: #1E88E5; /* Dark Blue for the example value */
            font-style: italic;
        }

        tr:nth-child(even) {
            background-color: #f9f9f9; /* Alternate row color */
        }

        /* Font size for the values in the first table */
        .first-table tbody td {
            font-size: 20px;
            text-align: center;
        }
        /* Center alignment for the header cells in the first table */
        .first-table thead th {
            text-align: center;
                }
    </style>
</head>
<body>
    <table class="first-table">
        <thead>
            <tr>
                <th>Total Scenarios</th>
                <th>Passed</th>
                <th>Failed</th>
                <th>Skipped</th>
            </tr>
        </thead>
        <tbody>
            <#list Scenarios as scenario>
                <#if scenario.totalscenarios??>
                    <tr>
                        <td>${scenario.totalscenarios}</td>
                        <td class="passed">${scenario.passed}</td>
                        <td class="failed">${scenario.failed}</td>
                        <td class="skipped">${scenario.skipped}</td>
                    </tr>
                </#if>
            </#list>
        </tbody>
    </table>
    <table>
        <thead>
            <tr>
                <th>Scenarios</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <#list Scenarios as scenario>
                <#if scenario.name??>
                    <tr>
                        <td>
                            ${scenario.name}
                            <#if scenario.example?exists>
                                <span class="example-label"> <br>Example:</span>
                                <span class="example-value">${scenario.example}</span>
                            </#if>
                        </td>
                        <td class="<#if scenario.status == 'Passed'>passed<#elseif scenario.status == 'Failed'>failed<#elseif scenario.status == 'Skipped'>skipped</#if>">${scenario.status}</td>
                    </tr>
                </#if>
            </#list>
        </tbody>
    </table>
</body>
</html>
