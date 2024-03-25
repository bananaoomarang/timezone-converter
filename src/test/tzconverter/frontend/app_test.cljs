(ns tzconverter.frontend.app-test
  (:require
   [cljs.test :refer-macros [deftest is are]]
   [tzconverter.frontend.app :refer [translate-timezone]]))

(deftest test-translate-timezone-success []
  (are [result datetime tz-from tz-to] (= result (translate-timezone datetime tz-from tz-to))
    "4th of January 2024 at 7:21 PM" "2024-01-04T14:21" "America/New_York" "Europe/London"
    "30th of March 2017 at 2:18 PM" "2017-03-30T09:18" "America/New_York" "Europe/London"
    "2nd of December 2019 at 1:01 AM" "2019-12-01T20:01" "Europe/Istanbul" "Asia/Shanghai"))

(deftest test-translate-timezone-failure []
  (is (= (translate-timezone "JUNK" "America/New_York" "Europe/London") nil)))
