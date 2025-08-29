import Footer from "./components/Footer";
import MainFrame from "./components/MainFrame";
import Title from "./components/Title";


function App() {
  return (
    <div id="webcrumbs">
      <div className="min-h-screen bg-gradient-to-br from-primary-50 via-gray-50 to-purple-50 p-4 md:p-6 lg:p-8">
        <Title />
        <MainFrame />
        <Footer />
      </div>
    </div>
  );
}

export default App;
