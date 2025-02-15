https://economic-felicity-kevinadriano1-bee2995b.koyeb.app/

## module 1
### Reflection 1

I applied some of the clean code principles that are being taught in the class such as meaningful naming conventions, ensuring that variable like productID will done its job as the ID of the product. I also followed the Single Responsibility Principle (SRP) by separating concerns between the controller, service, and repository layers. Additionally, I reduced code duplication by reusing logic in service and repository methods. I also implement secure coding practices that are introduced in the class, I implemented confirmation prompts for delete actions. I identified areas for improvement, such as adding input validation using annotations like @NotNull and @Size, and improving error handling with custom error pages. I also plan to enhance security by adding authorization checks to prevent unauthorized access to edit or delete endpoints.

### Reflection 2
After writing unit test I realized that unit test is an important part of software development. I think that the number of unit test depends on the complexity of the function being tested. in a class there should be enough test case to ensure the validity of different scenarios. achieving 100% code coverage does not imply that the code is free of bugs and error, we still have to check all possible input scenarios that might come up. If we were asked to create another functional test suite that verifies the number of items in the product list, I might end up duplicating the setup code from CreateProductFunctionalTest.java. while this might work, this might result in a code redundancy, which can reduce code cleanliness and quality. there are potential clean code issues that might appear, one of the example is the repetition of instance variable (serverPort, testBaseUrl, baseUrl), any changes in one file require adjusting to multiple files. the suggestion to improve this code redundancy is by introducing a new class that contain the common setup logic This class would handle browser setup, server port initialization, and URL management, which can be inherited by all functional test classes.

## module 2
### Reflection
During the exercise, I fixed permission issue in the dockerfile and ci.yml which was solved by adding chmod +x gradlew before running the build command. I also addressed a missing assertion in the test cases by ensuring meaningful validations were added to improve test reliability. I also added SonarCloud to my project, the reason is to analyze code quality and detect potential issues early in the development process. My current CI/CD implementation follows Continuous Integration principles by automatically running tests and analyzing code quality on every push or pull request. I also add Continuous Deployment feature where the workflow automatically deploys the latest changes to Koyeb whenever the main branch is updated. This automation will reduce manual errors, ensuring high code quality, and enabling faster, more reliable deployments.
