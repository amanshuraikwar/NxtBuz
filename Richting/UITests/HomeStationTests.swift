import XCTest

final class HomeStationTests: XCTestCase {
    func testSelectHomeFromBundledCatalogAndKeepItAfterRelaunch() {
        let app = XCUIApplication()
        app.launch()
        let home = app.buttons["homeStationSelection"]
        XCTAssertTrue(home.waitForExistence(timeout: 10))
        home.tap()
        let search = app.searchFields.firstMatch
        XCTAssertTrue(search.waitForExistence(timeout: 5))
        search.tap()
        search.typeText("Utrecht Centraal")
        app.buttons["Utrecht Centraal"].tap()
        // A location authorization prompt can follow selection on a fresh install.
        let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")
        let allow = springboard.buttons.containing(NSPredicate(format: "label BEGINSWITH %@", "Allow While Using")).firstMatch
        if allow.waitForExistence(timeout: 2) { allow.tap() }
        XCTAssertTrue(home.label.contains("Utrecht Centraal"))
        app.terminate()
        app.launch()
        XCTAssertTrue(home.waitForExistence(timeout: 10))
        XCTAssertTrue(home.label.contains("Utrecht Centraal"))
    }
}
