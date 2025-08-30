import ResultSection from "./ResultSection";
import UploadSection from "./UploadSection";
import React, { useState } from "react";
import About from "./About";

const MainFrame = () => {
  const [reportId, setReportId] = useState(null);
  return (
    <main className="max-w-7xl mx-auto">
      <div className="bg-white rounded-2xl shadow-xl overflow-hidden transition-all duration-500 hover:shadow-2xl transform hover:-translate-y-1">
        <div className="p-6 md:p-8">
          <div className="flex flex-col md:flex-row gap-8">
            <UploadSection setReportId={setReportId} />
            <ResultSection reportId={reportId} />
          </div>
        </div>
      </div>
      <About />
    </main>
  );
};

export default MainFrame;
