const fs = require('fs');
const replace = require('replace-in-file');
const parseString = require('xml2js').parseString;

function parsePOM() {
  let version = null;

  const pomXml = fs.readFileSync('pom.xml', 'utf8');

  parseString(pomXml, (err, result) => {
    if (err) {
      throw new Error('Failed to parse pom.xml: ' + err);
    }

    if (result.project.version && result.project.version[0]) {
      version = result.project.version[0];
    } else if (result.project.parent && result.project.parent[0] && result.project.parent[0].version && result.project.parent[0].version[0]) {
      version = result.project.parent[0].version[0];
    }
  });

  return {
    version: version
  };
}

function setBuildInformation() {
  const rootPath = 'src/main/webapp/';

  try {
    const timestamp = new Date().getTime();
    const pom = parsePOM();

    replace.sync({
      files: rootPath + 'environments/build.ts',
      from: [/version: '(.*)'/g, /timestamp: '(.*)'/g],
      to: ["version: '" + pom.version + "'", "timestamp: '" + timestamp + "'"],
      allowEmptyPaths: false
    });
  } catch (error) {
    console.error('Error setBuildInformation:', error);
    throw error;
  }
}

setBuildInformation();
