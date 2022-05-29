from selenium import webdriver
import time

options = webdriver.EdgeOptions()
options.use_chromium = True
# options.add_argument("--headless")
executable_path = r"D:/Drivers/browser_drivers/MicrosoftWebDriver.exe"

driver = webdriver.Edge(options=options, executable_path=executable_path)

js = """
    entries = window.performance.getEntries();
    console.log(entries)
    return entries
"""
driver.get("https://wttr.in/Mianyang?0")
entries = driver.execute_script(js)

for i in range(0, len(entries)):
    entry = entries[i]
    if(entry["entryType"] == "paint"):
        continue
    try:
        print("#" + str(i))
        print("name: " + entry["name"])
        print("size: %.2f"%(entry["transferSize"]) + "B")
        print("load time: %.2f"%(entry["duration"]) + "ms")
        print("---------------------------")
    except:
        pass
time.sleep(10)
driver.close()
