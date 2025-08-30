import React from "react";

const About = () => {
  return (
    <div className="mt-10 bg-white rounded-2xl shadow-xl overflow-hidden p-8 transform transition-all duration-500 hover:shadow-2xl hover:-translate-y-1">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">
        About Insight Generator
      </h1>
      <p className="text-gray-700 mb-4">
        DataForge AI is designed to generate realistic synthetic data for
        testing, machine learning, and research purposes. In today's world,
        high-quality data is essential for building reliable systems, training
        AI models, and testing applications without compromising sensitive
        information.
      </p>
      <p className="text-gray-700 mb-4">
        This project addresses the challenge of limited or sensitive datasets by
        allowing developers and researchers to create structured, safe, and
        customizable datasets on demand. It ensures faster development cycles,
        improved model accuracy, and safe experimentation environments.
      </p>
      <p className="text-gray-700 mb-4">
        <strong>Why it is necessary:</strong> For beginners in data analysis, it
        is often difficult to get meaningful ideas for creating charts and
        insights. Synthetic data provides a basic starting point, making it
        easier to practice building graphs, reports, and dashboards. This helps
        learners gain confidence in tools like Power BI and other visualization
        platforms while understanding real-world patterns without needing access
        to sensitive data.
      </p>

      <p className="text-gray-700 mb-4">
        <strong>Future possibilities:</strong> The platform can evolve into a
        fully automated AI-powered data generation hub capable of producing
        domain-specific datasets, integrating with cloud platforms, supporting
        real-time data streams, and enhancing AI model evaluation. Future
        versions might also automatically separate useful vs. less useful data
        insights, allowing users to focus on high-value information and reduce
        noise.
      </p>
    </div>
  );
};

export default About;
