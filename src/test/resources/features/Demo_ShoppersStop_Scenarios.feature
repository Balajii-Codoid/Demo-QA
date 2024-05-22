Feature: Demo_ShoppersStop_Application_Scenarios

  @Demo_ShoppersStop @C39
  Scenario Outline: User visits Shoppers Stop Application

    Given I am on Shoppers Stop Application
    When I search the product: "<WATCH>"
    Then I should see the product list

    Examples:
      | WATCH       |
      | Smart Watch |

  @Demo_ShoppersStop @C40
  Scenario Outline: User view the product details

    Given I am on Product list page
    When I click on "<Product>" from the list
    Then I should see Product Details page

    Examples:
      | Product         |
      | titan wearables |

  @Demo_ShoppersStop @C41
  Scenario: User views the Shirts section under Kids page

    Given I am on Product list page
    When I move to the "Shirts" under Kids section
    Then I should see Kids Shirt list