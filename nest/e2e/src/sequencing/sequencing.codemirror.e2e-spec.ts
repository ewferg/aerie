import { browser, by, element, logging, protractor } from 'protractor';
import { SequencingPage } from './sequencing.po';

describe('/sequencing.codemirror', () => {
  let page: SequencingPage;

  beforeEach(() => {
    page = new SequencingPage();
    page.navigateTo('sequencing');
    // wait needed because CodeMirror mounting is stalling the test causing it to exit due to timeout
    browser.waitForAngularEnabled(false);
    page.prepareForCodeMirrorTesting();
  });

  it('[C136005] WHEN a user starts typing a valid command, suggestions should appear', () => {
    page.sendKeysToCodeMirror('IAL_IMG_PRM_SE');

    expect(page.hintsContainer.isPresent()).toBe(true);
  });

  it('[C136006] WHEN a user types a valid stem and selects a suggestion then the editor SHOULD be filled with the command and the correct default parameters', () => {
    page.sendKeysToCodeMirror('IAL_IMG_PRM_SE');

    expect(page.hintsContainer.isPresent()).toBe(true);
    page.sendKeysToCodeMirror(protractor.Key.ENTER);

    // One off selectors for this test
    const commandToken = element(
      by.xpath(
        '//*[@id="middle-panel-area"]/main/sequencing-workspace/seq-editor/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span[1]',
      ),
    );
    const arg1Token = element(
      by.xpath(
        '//*[@id="middle-panel-area"]/main/sequencing-workspace/seq-editor/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span[2]',
      ),
    );
    const arg2Token = element(
      by.xpath(
        '//*[@id="middle-panel-area"]/main/sequencing-workspace/seq-editor/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span[3]',
      ),
    );
    const arg3Token = element(
      by.xpath(
        '//*[@id="middle-panel-area"]/main/sequencing-workspace/seq-editor/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span[4]',
      ),
    );
    const arg4Token = element(
      by.xpath(
        '//*[@id="middle-panel-area"]/main/sequencing-workspace/seq-editor/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span[5]',
      ),
    );

    expect(commandToken.getText()).toBe('IAL_IMG_PRM_SET');
    expect(arg1Token.getText()).toBe('"NAVF"');
    expect(arg2Token.getText()).toBe('1');
    expect(arg3Token.getText()).toBe('"GND"');
    expect(arg4Token.getText()).toBe('"GND"');
  });

  it("[C136008] WHEN a user types a valid stem and selects a suggestion that doesn't have parameters then the editor SHOULD be filled with just the command", () => {
    page.sendKeysToCodeMirror('HGA_HIST_PRM_S');

    expect(page.hintsContainer.isPresent()).toBe(true);

    page.sendKeysToCodeMirror(protractor.Key.ENTER);

    // One off selectors for this test
    // Holds the individual tokens for a command "CMD 1 2 3" would be [CMD, 1, 2, 3]
    // We are testing the case where the command is "CMD", expecting [CMD]
    const commandTokens = element.all(
      by.css('.CodeMirror-line > span:nth-child(1) span'),
    );
    expect(commandTokens.count()).toBe(1);
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser
      .manage()
      .logs()
      .get(logging.Type.BROWSER);
    expect(logs).not.toContain(
      jasmine.objectContaining({
        level: logging.Level.SEVERE,
      } as logging.Entry),
    );
  });
});