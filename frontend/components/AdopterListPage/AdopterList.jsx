import React from 'react';
import PropTypes from 'prop-types';

// components
import {
    DataGrid,
    gridPageCountSelector,
    GridPagination,
    useGridApiContext,
    useGridSelector,
    GridToolbar
} from '@mui/x-data-grid';

import MuiPagination from '@mui/material/Pagination';
import { Button } from '@mui/material';

import Image from 'next/image';

// style
import { gridStyle, moreBtn } from '@/styles/jss/components/AdopterListPage/adopterListStyle';
import { primaryColor, brownColor } from "@/styles/jss/animal-cloud-adoption.js";
import { createTheme, ThemeProvider } from '@mui/material/styles';

// Data
const rows = [
  { id: 1, adopter: 'A', currNum: 1, accumNum: 11, amount: 1000, animal: 'animal-1.jpg' },
  { id: 2, adopter: 'B', currNum: 2, accumNum: 12, amount: 2000, animal: 'animal-1.jpg' },
  { id: 3, adopter: 'C', currNum: 3, accumNum: 13, amount: 3000, animal: 'animal-1.png' },
  { id: 4, adopter: 'D', currNum: 4, accumNum: 14, amount: 4000, animal: '1.jpg' },
  { id: 5, adopter: 'E', currNum: 5, accumNum: 15, amount: 5000, animal: '1.jpg' },
  { id: 6, adopter: 'F', currNum: 6, accumNum: 16, amount: 6000, animal: '1.jpg' },
  { id: 7, adopter: 'G', currNum: 7, accumNum: 17, amount: 7000, animal: '1.jpg' },
  { id: 8, adopter: 'H', currNum: 8, accumNum: 18, amount: 8000, animal: '1.jpg' },
];

const columns = [
  { field: 'adopter', headerName: '認養人', width: 150, sortable: false },
  { field: 'currNum', headerName: <>當前認養<br />動物數量</>, width: 120 },
  { field: 'accumNum', headerName: <>累積認養<br />動物數量</>, width: 120 },
  { field: 'amount', headerName: '累積認養金額', width: 150 },
  { field: 'animal', headerName: '當前認養動物', width: 300, sortable: false,
    renderCell: (params) => (
      <Image
        src={`/animals/${params.value}`}
        alt={`Animal adopted by ${params.row.adopter}`}
        width={100}
        height={100}
      />
    ),
  },
  { field: 'more', headerName: '', width: 100, sortable: false, 
    renderCell: (params) => (
      // link 必須和 id 相同
      // <a href={`pages/adopters.adopterInfo/${params.row.id}`}>
      // 先導到沒有 id 的頁面
      <a href={`/adopters/adoptersInfo/`} target="_blank">
        <Button variant="outlined" sx={moreBtn}>查看更多</Button>
      </a>
    ),
  },
];

// Footer
function Pagination({ page, onPageChange, className }) {
  const apiRef = useGridApiContext();
  const pageCount = useGridSelector(apiRef, gridPageCountSelector);
  return (
    <MuiPagination
      color="primary"
      className={className}
      count={pageCount}
      page={page + 1}
      onChange={(event, newPage) => {
        onPageChange(event, newPage - 1);
      }}
    />
  );
}

Pagination.propTypes = {
  className: PropTypes.string,
  /**
    * Callback fired when the page is changed.
    *
    * @param {React.MouseEvent<HTMLButtonElement> | null} event The event source of the callback.
    * @param {number} page The page selected.
    */
  onPageChange: PropTypes.func.isRequired,
  /**
    * The zero-based index of the current page.
  */
  page: PropTypes.number.isRequired,
};

function CustomPagination(props) {
  return <GridPagination ActionsComponent={Pagination} {...props} />;
}

// styles
const theme = createTheme({
  palette: {
    primary: {
      main: brownColor, // set the primary color
    },
    secondary: {
      main: primaryColor, // set the secondary color
    },
    text: {
      // primary: brownColor,
      // secondary: primaryColor,
    },
    background: {
      default: '#fffff', // set the default background color
    },
    action: {
      active: primaryColor, // set the active action color (sorting arrows)
    },
    contrastThreshold: 3,
    tonalOffset: 0.2,
  },
});

export default function AdopterList() {

  return (
    <div style={{ height: 400, width: '100%' }}>
      <ThemeProvider theme={theme}>
        <DataGrid 
          rows={rows} 
          columns={columns} 
          disableColumnMenu
          disableRowSelectionOnClick
          initialState={{
            pagination: { paginationModel: { pageSize: 5 } },
          }}
          pageSizeOptions={[5]}
          components={{
              pagination: CustomPagination,
              Toolbar: GridToolbar,

          }}
          pagination
          slots={{
            pagination: CustomPagination,
          }}
          sx={gridStyle}
          />
      </ThemeProvider>
    </div>
  );
}
